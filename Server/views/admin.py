#coding:utf-8
from flask import Module

admin = Module(__name__)


@admin.route("/")
def index():
    return '<h1>Hello Administrator!</h1>'


@admin.route("/login")
def login():
    return '<h1>Login Page</h1>'


@admin.route("/logout")
def logout():
    return '<h1>Logout Page</h1>'

