from django.db import models


class Person(models.Model):
    # id auto-generated
    name = models.CharField(max_length=50)
    surnames = models.CharField(max_length=100)
    email = models.EmailField()
    password = models.CharField(max_length=150)
    password_token = models.CharField(max_length=20, null=True)
    token = models.CharField(max_length=20, unique=True, null=True)


class Product(models.Model):
    # id auto-generated
    name = models.CharField(max_length=255)
    description = models.CharField(max_length=2000)
    modelo = models.CharField(max_length=30, unique=True)
    color = models.CharField(max_length=200, default=None)
    price = models.DecimalField(max_digits=10, decimal_places=2)
    brand = models.CharField(max_length=255, default=None)
    image = models.CharField(max_length=500, default=None)


class ProductPerson(models.Model):
    person = models.ForeignKey(Person, on_delete=models.CASCADE)
    product = models.ForeignKey(Product, on_delete=models.CASCADE)
    date = models.DateField(auto_now=True)


class Cart(models.Model):
    # id auto-generated
    person = models.OneToOneField(Person, on_delete=models.CASCADE)
    product = models.ForeignKey(Product, on_delete=models.CASCADE)
    amount = models.IntegerField()


class Order(models.Model):
    # id auto-generated
    person = models.ForeignKey(Person, on_delete=models.CASCADE)
    product = models.ForeignKey(Product, on_delete=models.CASCADE)
    total = models.DecimalField(max_digits=10, decimal_places=2)
    date = models.DateField()
