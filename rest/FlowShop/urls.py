"""FlowShop URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from rest_api import endpoints

urlpatterns = [
    path('admin/', admin.site.urls),
    path('health', endpoints.health),
    path('v1/logged', endpoints.logged),
    path('v1/register', endpoints.register),
    path('v1/login', endpoints.login),
    path('v1/forget', endpoints.forgotten_password),
    path('v1/password', endpoints.reestablish_password),
    path('v1/products', endpoints.products),
]
