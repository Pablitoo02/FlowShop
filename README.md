![FlowShop](https://github.com/Pablitoo02/TFC-DAM/assets/129282925/41b68297-0ea3-4b41-b2cc-c780eba78368)

## Índice

* [Descripción](#descripción)

* [Estado del proyecto](#estado-del-proyecto)

* [Características](#características)

* [Tecnologías utilizadas](#tecnologías-utilizadas)

* [Instalación](#instalación)

## Descripción

FlowShop es una aplicación de ropa que ofrece una experiencia de compra en línea única para los entusiastas de la moda. Ya sea que estés buscando prendas elegantes, casuales o deportivas, FlowShop tiene todo cubierto. Con una amplia gama de opciones para hombres y mujeres, FlowShop se destaca por su selección de productos de alta calidad.

## Estado del proyecto

FlowShop es un aplicación funcional y lista para su uso.

## Características

- Explora una amplia colección de prendas de vestir.
- Filtra y busca prendas de vestir específicas por marca o nombre de la prenda.
- Visualiza información detallada del producto, incluyendo imágenes, descripciones...
- Aáde artículos a tu lista de favoritos.
- Añade artículos al carrito y procede al pago seguro.
- Autenticación de usuarios.

## Tecnologías utilizadas

- Python: Desarrollo del backend utilizando el framework Django, que ofrece una estructura robusta y flexible para la gestión de datos.
- Java: Desarrollo de la aplicación Android utilizando Android Studio y XML para el diseño de interfaces.
- SQLite: Gestión de la base de datos para almacenar información de productos y datos de usuarios.
- APIs RESTful: Integración de servicios externos para procesamiento de pagos y notificaciones.

## Instalación

1. Clonar o descargar el repositorio
git clone https://github.com/tu-nombre-usuario/FlowShop.git
2. Abrir la carpeta app en Android Studio y lanzar la app
3. En el terminal, situarse en la carpeta rest/
4. Intalar las dependencias necesarias
pip install -r requirements.txt
5. Moverse a la carpeta rest/
6. Ejecutar python manage.py runserver
7. Ya puedes probar FlowShop!

Para probarlo en un móvil habría que:

1. Tener el móvil y el ordenador conectados a la misma red
2. Con ipconfig conocer la ip interna del ordenador
3. En la clase RestClient, cambiar la variable LOCALHOST por http://ipOrdenador:8000
4. Asignar a BASE_URL la variable LOCALHOST
5. Al iniciar el servidor usar python manage.py runserver 0.0.0.0:8000
6. Ya puedes probar FlowShop en tu dispositivo móvil!
