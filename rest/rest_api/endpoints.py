import secrets
import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from itertools import count

import bcrypt
import json

import results as results
from django.db.models import Q, Sum
from django.db.models.functions import Round
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
                             token=None,
                             password_token=None

                             )
        user_object.save()
        return JsonResponse({"is_created": True}, status=201)


# Login
@csrf_exempt
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


# Reestablecer contraseña
@csrf_exempt
def reestablish_password(request):
    if request.method != 'POST':
        return JsonResponse({"error": "Método http no soportado"})

    token_cabeceras = request.headers.get("Token")
    if token_cabeceras is None:

        body_json = json.loads(request.body)
        try:
            json_token = body_json['passwordToken']
            json_password = body_json['newPassword']
        except KeyError:
            return JsonResponse({"error": "Faltán parámetros"}, status=400)
        try:
            user = Person.objects.get(password_token=json_token)
        except Person.DoesNotExist:
            return JsonResponse({"error": "Token inválido"}, status=404)

        salted_and_hashed_pass = bcrypt.hashpw(json_password.encode('utf8'), bcrypt.gensalt()).decode('utf8')
        user.password_token = None
        user.password = salted_and_hashed_pass
        user.save()

        return JsonResponse({"Mensaje": "Contraseña cambiada"}, status=201)

    else:
        try:

            body_json = json.loads(request.body)

            try:
                json_password = body_json['oldPassword']
                new_json_password = body_json['newPassword']

            except KeyError:
                return JsonResponse({"error": "Faltán parámetros"}, status=400)

            u = Person.objects.get(token=token_cabeceras)

            if bcrypt.checkpw(json_password.encode('utf8'), u.password.encode('utf8')):
                salted_and_hashed_pass = bcrypt.hashpw(new_json_password.encode('utf8'), bcrypt.gensalt()).decode(
                    'utf8')
                u.password = salted_and_hashed_pass
                u.save()

                return JsonResponse({"Mensaje": "Contraseña cambiada"}, status=201)
            else:
                return JsonResponse({"error": "Contraseña no válida"}, status=404)



        except Person.DoesNotExist:
            return JsonResponse({"error": "Usuario no logeado"}, status=401)


# Lista de productos
def products(request):
    if request.method != 'GET':
        return JsonResponse({"error": "Método HTTP no soportado"}, status=405)

    # Cantidad de resultados por página
    size = request.GET.get("size", None)

    # Posicion de la primera sudadera a mostrar en la página
    offset = request.GET.get("offset", None)

    # Nombre de la sudadera
    name = request.GET.get("name", None)

    if size is None:
        if offset is None:
            products = Product.objects.filter(Q(name__contains=name) | Q(brand__contains=name)).values_list('name',
                                                                                                            'price',
                                                                                                            'brand',
                                                                                                            'modelo',
                                                                                                            'image')
        else:
            try:
                offset = int(offset)
            except ValueError:
                return JsonResponse({"error": "Parámetro offset erróneo"}, status=400)

            return JsonResponse({"error": "Faltán parámetros"}, status=400)
    else:
        try:
            size = int(size)
        except ValueError:
            return JsonResponse({"error": "Parámetro size erróneo"}, status=400)

        if offset is None:
            return JsonResponse({"error": "Faltán parámetros"}, status=400)
        else:
            try:
                offset = int(offset)
            except ValueError:
                return JsonResponse({"error": "Parámetro offset erróneo"}, status=400)

            if name is None or len(name) == 0:
                products = Product.objects.all().values_list('name',
                                                             'price',
                                                             'brand',
                                                             'modelo',
                                                             'image')[offset:offset + size]
            else:
                products = Product.objects.filter(Q(name__contains=name) | Q(brand__contains=name)).values_list('name',
                                                                                                                'price',
                                                                                                                'brand',
                                                                                                                'modelo',
                                                                                                                'image')[
                           offset:offset + size]

    count = Product.objects.count()

    results = []
    if products is not None:
        for product in products:
            results.append({"name": product[0],
                            "price": product[1],
                            "brand": product[2],
                            "modelo": product[3],
                            "image": product[4]})

    return JsonResponse({"count": count, "results": results}, safe=False)


# Detalle del producto
def product(request, modelo):
    if request.method != "GET":
        return JsonResponse({"error": "HTTP method not supported"}, status=405)

    try:
        product = Product.objects.get(modelo=modelo)
    except Product.DoesNotExist:
        return JsonResponse({"error": "No existe"}, status=404)

    return JsonResponse(
        {"modelo": modelo, "image": product.image, "name": product.name, "price": product.price, "brand": product.brand,
         "description": product.description, "model": product.modelo, "color": product.color})


# Comprobación, adición, borrado y recogida de favoritos
@csrf_exempt
def favorites(request, modelo):
    try:
        p = Product.objects.get(modelo=modelo)
    except Product.DoesNotExist:
        return JsonResponse({"error": "No existe"}, status=404)

    token_cabeceras = request.headers.get("Token")
    if token_cabeceras is None:
        return JsonResponse({"error": "Falta token en la cabecera"}, status=401)
    else:
        try:
            u = Person.objects.get(token=token_cabeceras)
        except Person.DoesNotExist:
            return JsonResponse({"error": "Usuario no logeado"}, status=401)

    if request.method == "PUT":
        try:
            product_person = ProductPerson.objects.get(product=p, person=u)
            return JsonResponse({"status": "Todo OK"}, status=200)
        except ProductPerson.DoesNotExist:
            new_product_person = ProductPerson(product=p, person=u)
            new_product_person.save()
            return JsonResponse({"status": "Todo OK"}, status=200)

    elif request.method == "DELETE":
        try:
            product_person = ProductPerson.objects.get(product=p, person=u)
            product_person.delete()
            return JsonResponse({"status": "Todo OK"}, status=200)
        except ProductPerson.DoesNotExist:
            return JsonResponse({"status": "Todo OK"}, status=200)

    elif request.method == "GET":
        try:
            product_person = ProductPerson.objects.get(product=p, person=u)
            return JsonResponse({"status": "Todo OK"}, status=200)
        except ProductPerson.DoesNotExist:
            return JsonResponse({"error": "No existe en favoritos"}, status=404)


# Adición, borrado y recogida de productos en el carrito
@csrf_exempt
def cart(request, modelo):
    try:
        p = Product.objects.get(modelo=modelo)
    except Product.DoesNotExist:
        return JsonResponse({"error": "No existe"}, status=404)

    token_cabeceras = request.headers.get("Token")
    if token_cabeceras is None:
        return JsonResponse({"error": "Falta token en la cabecera"}, status=401)
    else:
        try:
            u = Person.objects.get(token=token_cabeceras)
        except Person.DoesNotExist:
            return JsonResponse({"error": "Usuario no logeado"}, status=401)

    if request.method == "PUT":
        try:
            cart = Cart.objects.get(product=p, person=u)
            return JsonResponse({"status": "Todo OK"}, status=200)
        except Cart.DoesNotExist:
            new_cart = Cart(product=p, person=u)
            new_cart.save()
            return JsonResponse({"status": "Todo OK"}, status=200)

    elif request.method == "DELETE":
        try:
            cart = Cart.objects.get(product=p, person=u)
            cart.delete()
            return JsonResponse({"status": "Todo OK"}, status=200)
        except ProductPerson.DoesNotExist:
            return JsonResponse({"status": "Todo OK"}, status=200)


# Lista de favoritos
def favorites_list(request):
    token_cabeceras = request.headers.get("Token")
    if token_cabeceras is None:
        return JsonResponse({"error": "Falta token en la cabecera"}, status=401)
    else:
        try:
            u = Person.objects.get(token=token_cabeceras)
        except Person.DoesNotExist:
            return JsonResponse({"error": "Usuario no logeado"}, status=401)

    if request.method == "GET":
        product_persons = ProductPerson.objects.filter(person__token=token_cabeceras).values_list('product__name',
                                                                                                  'product__price',
                                                                                                  'product__brand',
                                                                                                  'product__modelo',
                                                                                                  'product__image')

        favorites = []
        if product_persons is not None:
            for product_person in product_persons:
                favorites.append({"product__name": product_person[0],
                                  "product__price": product_person[1],
                                  "product__brand": product_person[2],
                                  "product__modelo": product_person[3],
                                  "product__image": product_person[4]})

        return JsonResponse({"favorites": favorites}, safe=False)
    return JsonResponse({"error": "HTTP method not supported"}, status=405)


# Perfil
@csrf_exempt
def profile(request):
    token_cabeceras = request.headers.get("Token")
    if token_cabeceras is None:
        return JsonResponse({"error": "Falta token en la cabecera"}, status=401)
    else:
        try:
            u = Person.objects.get(token=token_cabeceras)
        except Person.DoesNotExist:
            return JsonResponse({"error": "Usuario no logeado"}, status=401)

    if request.method == "GET":
        json_response = {
            "name": u.name,
            "surnames": u.surnames,
            "email": u.email
        }

        return JsonResponse(json_response, status=200)

    if request.method == "PUT":
        body_json = json.loads(request.body)

        u.name = body_json["name"]
        u.surnames = body_json["surnames"]
        u.email = body_json["email"]
        u.save()
        return JsonResponse({"status": "Todo OK"}, status=200)


# Lista del carrito
@csrf_exempt
def cart_list(request):
    token_cabeceras = request.headers.get("Token")
    if token_cabeceras is None:
        return JsonResponse({"error": "Falta token en la cabecera"}, status=401)
    else:
        try:
            u = Person.objects.get(token=token_cabeceras)
        except Person.DoesNotExist:
            return JsonResponse({"error": "Usuario no logeado"}, status=401)

    if request.method == "GET":
        carts = Cart.objects.filter(person__token=token_cabeceras).values_list('product__name',
                                                                               'product__price',
                                                                               'product__brand',
                                                                               'product__modelo',
                                                                               'product__image')

        products_cart = []
        if carts is not None:
            for cart in carts:
                products_cart.append({"product__name": cart[0],
                                      "product__price": cart[1],
                                      "product__brand": cart[2],
                                      "product__modelo": cart[3],
                                      "product__image": cart[4]})

        # Para sumar los precio y saber el total del carrito, solo con 2 decimales
        total_price = Cart.objects.filter(person__token=token_cabeceras).aggregate(
            total_price=Round(Sum('product__price'), 2)
        )['total_price']

        return JsonResponse({"products_cart": products_cart, "total_price": total_price}, safe=False)

    if request.method == "DELETE":
        Cart.objects.all().delete()
