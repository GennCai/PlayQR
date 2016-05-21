#coding:utf-8
from flask import make_response, jsonify, request, render_template
from flask.ext.restful import Resource, reqparse, fields, marshal
from flask.ext.httpauth import HTTPBasicAuth
from wtf import TakesPostForm
from datetime import datetime
import os
import werkzeug

auth = HTTPBasicAuth()
UPLOAD_FOLDER = '/home/genn/Public'
ALLOWED_EXTENSIONS = set(['txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'])


@auth.get_password
def get_password(username):
    from app import User, db
    user = User.query.filter_by(username=username).first()
    if user is not None:
        return user.password
    return None


@auth.error_handler
def error_handler():
    return make_response(jsonify({'error': 'Unauthorized access'}), 401)


def get_user(username):
    from app import User
    user = User.query.filter_by(username=username).first()
    if user is not None:
        return user
    return None


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS

parse = reqparse.RequestParser()
parse.add_argument('id', type=int, location=['form', 'json'])
parse.add_argument('image_name', type=str, location=['form', 'json'])
parse.add_argument('decode_data', type=str, location=['form', 'json'])
parse.add_argument('time', type=str,  location=['form', 'json'])
#parse.add_argument('location', type=str, location=['form', 'json'])


class TasksAPI(Resource):
    decorators = [auth.login_required]

    def __init__(self):
        self.form = TakesPostForm()
        super(TasksAPI, self).__init__()

    def get(self):
        from app import db, User, Image
        username = request.authorization.username
        user = get_user(username)
        if user is not None:
            images_db = []
            if user.username == 'admin':
                images = Image.query.all()
            else:
                images = Image.query.filter_by(user_id=user.id).all()
            if len(images) == 0:
                return {'message': 'you have not upload image'}, 205
            for image in images:
                image_db = {
                    'id': image.id,
                    'image_name': image.image_name,
                    'decode_data': image.decode_data,
                    'time': image.time.strftime('%Y-%m-%d %H:%M:%S'),
                    'location': image.location
                }
                images_db.append(image_db)
            return images_db
        return {'error': 'cannot find user: ' + username}, 401

    def post(self):
        from app import db, User, Image
        simple_auth = request.authorization
        username = simple_auth.username
    #    password = simple_auth.password
        image_data = request.files['image_data']

        user = get_user(username)
        if user is not None and image_data:
            filename = image_data.filename
            if Image.query.filter_by(image_name=filename).first() is not None:
                return {'error': 'the summit image name have exist'}, 407
            image_data.save(os.path.join(user.upload_folder, filename))
            args = parse.parse_args()
            decode_data = args.get('decode_data')
            time = args.get('time') #时间格式 2016-05-12 10:19:19
            if time is None or len(time) != 17:
                time = datetime.now()
            else:
                time = datetime.strptime(time, '%Y-%m-%d %H:%M:%S')
            location = request.form['location']

            new_image = Image(image_name=filename, decode_data=decode_data,
                              time=time, location=location, user=user)
            db.session.add(new_image)
            db.session.commit()
            return {'success': 'you have uploaded your data'}, 201
        return {'failed': 'same terrible things seemed happen'}, 501


class TaskAPI(Resource):
    decorators = [auth.login_required]

    def __init__(self):
        super(TaskAPI, self).__init__()

    def get(self, id):
        from app import Image, User
        user = get_user(request.authorization.username)
        if user.username == 'admin':
            image = Image.query.filter_by(id=id).first()
        else:
            image = Image.query.filter_by(id=id, user_id=user.id).first()
        if image is None:
            return {'error': 'can not find request source'}, 408
        image_db = {
            'id': image.id,
            'image_name': image.image_name,
            'decode_data': image.decode_data,
            'time': image.time.strftime('%Y-%m-%d %H:%M:%S'),
            'location': image.location
        }
        return image_db

    def put(self, id):
        from app import db, Image, User
        args = parse.parse_args()
        user = get_user(request.authorization.username)
        if user.username == 'admin':
            image = Image.query.filter_by(id=id).first()
        else:
            image = Image.query.filter_by(id=id, user_id=user.id).first()
        if image is None:
            return {'error': 'the update resource is not exist'}, 409
        if image.image_name != args.get('image_name'):
            image.image_name = args.get('image_name')
            old_file = os.path.join(user.upload_folder, image.image_name)
            if os.path.exists(old_file):
                new_file = os.path.join(user.upload_folder, args.get('image_name'))
                os.rename(old_file, new_file)

        image.decode_data = args.get('decode_data')
        time = args.get('time')
        if time is None or len(time) != 17:
            time = datetime.now()
        else:
            time = datetime.strptime(time, '%Y-%m-%d %H:%M:%S')
        image.time = time
        image.location = request.form['location']
        db.session.commit()
        return {'message': 'TakeAPI PUT'}

    def delete(self, id):
        from app import db, Image, User
        user = get_user(request.authorization.username)
        if user.username == 'admin':
            image = Image.query.filter_by(id=id).first()
        else:
            image = Image.query.filter_by(id=id, user_id=user.id).first()
        if image is None:
            return {'error': 'the delete resource is not exist'}, 209
        db.session.delete(image)
        db.session.commit()
        image_path = os.path.join(user.upload_folder, image.image_name)
        if os.path.exists(image_path):
            os.remove(image_path)
        return {'message': 'TakeAPI DELETE'}


