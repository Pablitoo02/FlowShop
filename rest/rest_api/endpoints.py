import secrets
import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

import bcrypt
import json
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt

from .models import Person, Product, ProductPerson, Cart, Order


def health(request):
    return JsonResponse({"status": "alive"}, status=200)


# Comprobación de si ya está registrado
def logged(request):
    if request.method != 'GET':
        return JsonResponse({"error": "Método http no soportado"})

    sessionToken = request.headers.get('Token')
    try:
        user = Person.objects.get(token=sessionToken)
    except Person.DoesNotExist:
        return JsonResponse({"error": "Token inválido"}, status=404)

    return JsonResponse({"status": "ok"}, status=200)


# Registro
@csrf_exempt
def register(request):
    if request.method != "POST":
        return JsonResponse({'error': 'HTTP method unsupported'}, status=405)

    body_json = json.loads(request.body)

    try:
        json_username = body_json['name']
        json_surname = body_json['surnames']
        json_email = body_json['email']
        json_birthdate = body_json['birthdate']
        json_password = body_json['password']


    except KeyError:
        return JsonResponse({'error': 'Missing paramenter in JSON'}, status=400)

    try:
        alreadyregister = Person.objects.get(email=json_email)
        return JsonResponse({"error": "Email already registered"}, status=409)
    except Person.DoesNotExist:
        salted_and_hashed_pass = bcrypt.hashpw(json_password.encode('utf8'), bcrypt.gensalt()).decode('utf8')
        user_object = Person(email=json_email,
                             password=salted_and_hashed_pass,
                             name=json_username,
                             surnames=json_surname,
                             birthday=json_birthdate,
                             token=None,
                             password_token=None

                             )
        user_object.save()
        return JsonResponse({"is_created": True}, status=201)


# Login
def login(request):
    if request.method != 'POST':
        return JsonResponse({"error": "Método HTTP no soportado"}, status=405)

    body_json = json.loads(request.body)
    try:
        json_email = body_json['email']
        json_password = body_json['password']
    except KeyError:
        return JsonResponse({"error": "Faltán parámetros"}, status=400)

    try:
        db_user = Person.objects.get(email=json_email)
    except Person.DoesNotExist:
        return JsonResponse({"error": "Usuario no encontrado"}, status=404)

    if bcrypt.checkpw(json_password.encode('utf8'), db_user.password.encode('utf8')):
        random_token = secrets.token_hex(10)
        db_user.token = random_token
        db_user.save()
        return JsonResponse({"sessionToken": random_token}, status=201)
    else:
        return JsonResponse({"error": "Contraseña incorrecta"}, status=401)


# Contraseña olvidada
@csrf_exempt
def forgotten_password(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Método HTPP no soportado'}, status=405)
    body_json = json.loads(request.body)

    try:
        json_email = body_json['email']
    except KeyError:
        return JsonResponse({"error": "Campo vacio"}, status=400)

    try:
        db_user = Person.objects.get(email=json_email)
    except Person.DoesNotExist:
        return JsonResponse({"error": "Usuario no encontrado"}, status=404)

    password_token = secrets.token_hex(10)
    db_user.password_token = password_token
    db_user.save()

    # Creación del mensaje
    msg = MIMEMultipart("alternative")
    message = f"""
        <html>
        <body>
            <p align= center> ¡Hola, <i>{db_user.name}</i>! </p> <br>
            <p align= center> Tu token es el siguiente </p>
            <p align= center> {password_token} </b> </p>
        </body>
        <html>
         """

    # Parámetros del mensaje
    password_mail = "pablofp02"
    msg['From'] = "phermidaa@fpcoruna.afundacion.org"
    msg['To'] = db_user.email  # "phermidaa@fpcoruna.afundacion.org"
    msg['Subject'] = "Restablecer la contraseña"

    # Adición del cuerpo del mensaje
    msg.attach(MIMEText(message, 'html'))

    # Creación del servidor
    server = smtplib.SMTP('smtp.gmail.com: 587')
    server.starttls()

    # Inicio de sesión para enviar el correo
    server.login(msg['From'], password_mail)

    # Envio del correo a través del servidor
    server.sendmail(msg['From'], msg['To'], msg.as_string())
    server.quit()
    return JsonResponse({"successful": "OK"}, status=201)