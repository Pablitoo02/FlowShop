# Generated by Django 4.1.2 on 2023-05-30 17:55

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('rest_api', '0003_product_brand_product_color_product_image_and_more'),
    ]

    operations = [
        migrations.AlterField(
            model_name='product',
            name='modelo',
            field=models.CharField(max_length=30, unique=True),
        ),
    ]