# Generated by Django 4.1.2 on 2023-05-18 21:26

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Category',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=50)),
            ],
        ),
        migrations.CreateModel(
            name='Person',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=50)),
                ('surnames', models.CharField(max_length=100)),
                ('email', models.EmailField(max_length=254)),
                ('birthday', models.DateField()),
                ('password', models.CharField(max_length=150)),
                ('password_token', models.CharField(max_length=20, null=True)),
                ('token', models.CharField(max_length=20, null=True, unique=True)),
            ],
        ),
        migrations.CreateModel(
            name='Product',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=255)),
                ('description', models.CharField(max_length=200)),
                ('price', models.DecimalField(decimal_places=2, max_digits=10)),
                ('category', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='rest_api.category')),
            ],
        ),
        migrations.CreateModel(
            name='ProductPerson',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('date', models.DateField(auto_now=True)),
                ('person', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='rest_api.person')),
                ('product', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='rest_api.product')),
            ],
        ),
        migrations.CreateModel(
            name='Order',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('total', models.DecimalField(decimal_places=2, max_digits=10)),
                ('date', models.DateField()),
                ('person', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='rest_api.person')),
                ('product', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='rest_api.product')),
            ],
        ),
        migrations.CreateModel(
            name='Cart',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('amount', models.IntegerField()),
                ('person', models.OneToOneField(on_delete=django.db.models.deletion.CASCADE, to='rest_api.person')),
                ('product', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='rest_api.product')),
            ],
        ),
    ]
