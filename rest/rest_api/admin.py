from django.contrib import admin
from .models import Person, Product, ProductPerson, Cart, Order

# Register your models here.
admin.site.register(Person)
admin.site.register(Product)
admin.site.register(ProductPerson)
admin.site.register(Cart)
admin.site.register(Order)