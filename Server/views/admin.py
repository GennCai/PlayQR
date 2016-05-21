#coding:utf-8
from flask import Module, make_response, jsonify, render_template, request, redirect, url_for
from flask.ext.restful import Resource
from flask.ext.httpauth import HTTPBasicAuth
from datetime import datetime
import os
from wtf import LoginForm, UserEditForm

admin = Module(__name__)
auth = HTTPBasicAuth()


@auth.get_password
def get_password(username):
    if username == 'admin':
        return 'gxs42526037'
    else:
        return None


@auth.error_handler
def error_handler():
    return make_response(jsonify({'error': 'Unauthorized access'}), 401)


@admin.route("/")
@auth.login_required
def index():
    from app import db, Image, User
    users_data = []
    images_data = []
    users = User.query.all()
    images = Image.query.all()
    for user in users:
        user_data = {
            'id': user.id,
            'username': user.username,
            'password': user.password,
            'upload_folder': user.upload_folder
        }
        users_data.append(user_data)
    for image in images:
        image_data = {
            'id': image.id,
            'image_name': image.image_name,
            'decode_data': image.decode_data,
            'time': image.time,
            'location': image.location,
            'user_id': image.user_id
        }
        images_data.append(image_data)
    return render_template('admin.html', users_data=users_data, images_data=images_data)


@admin.route("/login")
def login():
    return ""


@admin.route("/logout")
def logout():
    return '<h1>Logout Page</h1>'


class UserAPI(Resource):
    decorators = [auth.login_required]

    def __init__(self):
        super(UserAPI, self).__init__()

    def get(self, id):
        return 'user' + str(id) + 'get'

    def post(self, id):
        from app import db, User, Image
        if request.form['_method'] == 'put':
            user = {
                'id': request.form['id'],
                'username': request.form['username'],
                'password': request.form['password'],
                'upload_folder': request.form['upload_folder']
            }
            if request.form.get('edit') == 'edit':
                user = User.query.filter_by(username=request.form['username']).first()
                user.username = request.form['username']
                user.password = request.form['password']
                if user.upload_folder != request.form['upload_folder']:
                    user.upload_folder = request.form['upload_folder']
                db.session.commit()
                return make_response(redirect('/admin'))
            return make_response(render_template('user_api_put.html', user=user))
        if request.form['_method'] == 'delete':
            user = User.query.filter_by(id=id).first()
            if os.path.exists(user.upload_folder):
                os.system("rm -rf " + user.upload_folder)
            images = Image.query.filter_by(user_id=id).all()
            for image in images:
                db.session.delete(image)
            db.session.delete(user)
            db.session.commit()
            return make_response(redirect('/admin'))

    def put(self, id):
        return 'user' + str(id) + 'put'

    def delete(self, id):
        return 'user' + str(id) + 'delete'


class ImageAPI(Resource):
    decorators = [auth.login_required]

    def __init__(self):
        super(ImageAPI, self).__init__()

    def post(self, id):
        from app import db, User, Image
        if request.form['_method'] == 'put':
            image = {
                'id': request.form.get('id'),
                'image_name': request.form.get('image_name'),
                'decode_data': request.form.get('decode_data'),
                'time': request.form.get('time'),
                'location': request.form.get('location'),
                'user_id': request.form.get('user_id')
            }
            if request.form.get('edit') == 'edit':
                image = Image.query.filter_by(id=id).first()
                if image.image_name != request.form.get('image_name'):
                    user = User.query.filter_by(id=image.user_id).first()
                    old_file = os.path.join(user.upload_folder, image.image_name)
                    if os.path.exists(old_file):
                        new_file = os.path.join(user.upload_folder, request.form['image_name'])
                        os.rename(old_file, new_file)
                    image.image_name = request.form.get('image_name')
                time = request.form.get('time')
                if time is None or len(time) != 17:
                    time = datetime.now()
                else:
                    time = datetime.strptime(time, '%Y-%m-%d %H:%M:%S')
                image.time = time
                image.decode_data = request.form.get('decode_data')
                image.location = request.form.get('location')
                db.session.commit()
                return make_response(redirect('/admin'))
            return make_response(render_template('image_api_put.html', image=image))
        if request.form['_method'] == 'delete':
            image = Image.query.filter_by(id=id).first()
            user = User.query.filter_by(id=image.user_id).first()
            old_file = os.path.join(user.upload_folder, image.image_name)
            if os.path.exists(old_file):
                os.remove(old_file)
            db.session.delete(image)
            db.session.commit()
            return make_response(redirect('/admin'))
