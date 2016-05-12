from flask import make_response, jsonify, request, render_template
from flask.ext.restful import Resource, reqparse, fields, marshal
from flask.ext.httpauth import HTTPBasicAuth
from wtf import TakesPostForm
from datetime import datetime
import os

auth = HTTPBasicAuth()
UPLOAD_FOLDER = '/home/genn/Public'
ALLOWED_EXTENSIONS = set(['txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'])


@auth.get_password
def get_password(username):
    from app import User
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
parse.add_argument('image_data', type=file, location='form')
parse.add_argument('decode_data', type=str, location='form')
parse.add_argument('take_time', type=str,  location='form')
parse.add_argument('position', type=str)


class TasksAPI(Resource):
    decorators = [auth.login_required]
  #  form = TakesPostForm()

    def __init__(self):
        self.form = TakesPostForm()
        super(TasksAPI, self).__init__()

    def get(self):
        return {'message': 'TakesAPI GET'}

    def post(self):
        from app import db, User, Image
        simple_auth = request.authorization
        username = simple_auth.username
    #    password = simple_auth.password
        image_data = request.files['image_data']

        user = get_user(username)
        if user is not None and image_data:
            filename = os.path.join(user.upload_folder, image_data.filename)
            image_data.save(filename)

            args = parse.parse_args()
            decode_data = args.get('decode_data')
            take_time = args.get('take_time')
            take_time = datetime.strptime(take_time, '%Y-%m-%d %H:%M:%S')
            position = args.get('position')

            new_image = Image(image_data=filename, decode_data=decode_data,
                              take_time=take_time, position=position, user=user)
            db.session.add(new_image)
            db.session.commit()
            return {'success': 'you have uploaded your data'}, 201
        return {'failed': 'same terrible things seemed happen'}, 501


class TaskAPI(Resource):
    decorators = [auth.login_required]

    def __init__(self):
        super(TaskAPI, self).__init__()

    def get(self, id):
        return {'message': 'TakeAPI GET'}

    def put(self, id):
        return {'message': 'TakeAPI PUT'}

    def delete(self, id):
        return {'message': 'TakeAPI DELETE'}


