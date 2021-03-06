#coding:utf-8
from flask import Flask, render_template, url_for, request, redirect, jsonify, make_response, send_from_directory
from flask.ext.sqlalchemy import SQLAlchemy
from flask.ext.restful import Api
from views.admin import admin, UserAPI, ImageAPI
from views.wtf import RegisterForm, LoginForm, TakesPostForm
from views.restful import TasksAPI, TaskAPI, auth
from datetime import datetime
from flask.ext.bootstrap import Bootstrap
import os

app = Flask(__name__)
app.debug = True
app.register_module(admin, url_prefix='/admin')
app.config['SECRET_KEY'] = 'HFU564"LI4565&GFVB^&TYT'
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://root:788563@localhost/play_qr'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = True
app.config['UPLOAD_FOLDER'] = '/home/genn/Public/'
db = SQLAlchemy(app)
bootstrap = Bootstrap(app)

api = Api(app)
api.add_resource(TasksAPI, '/playqr/api/v1.0/tasks', endpoint='tasks')
api.add_resource(TaskAPI, '/playqr/api/v1.0/task/<int:id>', endpoint='task')
api.add_resource(UserAPI, '/playqr/api/v1.0/user/<int:id>', endpoint='user_api')
api.add_resource(ImageAPI, '/playqr/api/v1.0/image/<int:id>', endpoint='image_api')


@app.route('/test')
@auth.login_required
def test():
    form = TakesPostForm()
    return render_template('takes_post.html', form=form)


class Image(db.Model): #定义表images
    __tablename__ = 'images'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    image_name = db.Column(db.String(240), index=True)
    decode_data = db.Column(db.String(240), default='none')
    time = db.Column(db.DateTime, default=datetime.now())
    location = db.Column(db.String(240), default='none')

    user_id = db.Column(db.Integer, db.ForeignKey('users.id'))
    user = db.relationship('User', backref=db.backref('images', lazy='dynamic'))

    def __init__(self, image_name, decode_data, time, location, user):
        self.image_name = image_name
        self.decode_data = decode_data
        self.time = time
        self.location = location
        self.user = user

    def __repr__(self):
        return '<Image %r>' % self.image_data


class User(db.Model):
    __tablename__ = 'users'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    username = db.Column(db.String(80), unique=True, index=True, nullable=False)
    password = db.Column(db.String(120), nullable=False)
    upload_folder = db.Column(db.String(240), unique=True, nullable=False)

    def __init__(self, username, password):
        self.username = username
        self.password = password
        self.upload_folder = os.path.abspath(os.path.join(app.config['UPLOAD_FOLDER'], username))
        if not os.path.exists(self.upload_folder):
            os.mkdir(self.upload_folder)

    def __repr__(self):
        return '<User %r>' % self.username


@app.route('/')
def hello():
    return render_template('index.html')


@app.route('/login', methods=['GET', 'POST'])
def login():
    form = LoginForm()
    simple_auth = request.authorization
    if request.method == 'POST':
        try:
            username = simple_auth.username
            password = simple_auth.password
        except AttributeError:
            return make_response(jsonify({'error': 'Unauthorized access'}), 401)
        user = User.query.filter_by(username=username).first()
        if user is not None:
            if password == user.password:
                return jsonify({'message': 'login successfully'}), 200
            else:
                return make_response(jsonify({'error': 'your password is incorrect'}), 402)
        else:
            return make_response(jsonify({'error': 'this username is incorrect'}), 403)
    else:
        return render_template('login.html', form=form)


@app.route('/register', methods=['GET', 'POST'])
def register():
    form = RegisterForm()
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        if User.query.filter_by(username=username).first() is None:
            user = User(username, password)
            db.session.add(user)
            db.session.commit()
            return jsonify({'message': 'register successfully'}), 201
        else:
            return make_response(jsonify({'error': 'this name has benn register'}), 405)
    return render_template('register.html', form=form)


@app.route('/download/<filename>')
@auth.login_required
def download(filename):
    user = User.query.filter_by(username=request.authorization.username).first()
    directory = user.upload_folder
    return send_from_directory(directory=directory, filename=filename)


@app.route('/images/<int:id>')
def show_image(id):
    image = Image.query.filter_by(id=id).first()
    user = User.query.filter_by(id=image.user_id).first()
    directory = user.upload_folder
    return send_from_directory(directory=directory, filename=image.image_name)

if __name__ == '__main__':
    app.run(host='192.168.1.110')
