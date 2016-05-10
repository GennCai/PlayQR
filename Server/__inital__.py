from flask import Flask, render_template, url_for, request, redirect
from flask.ext.sqlalchemy import SQLAlchemy
from flask.ext.restful import Api
from views.admin import admin
from views.wtf import RegisterForm, LoginForm
from views.restful import TasksAPI, TaskAPI
from datetime import datetime

app = Flask(__name__)
app.register_module(admin, url_prefix='/admin')
app.config['SECRET_KEY'] = 'HFU564"LI4565&GFVB^&TYT'
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://root:788563@localhost/play_qr'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = True
db = SQLAlchemy(app)

api = Api(app)
api.add_resource(TasksAPI, '/playqr/api/v1.0/tasks', endpoint='tasks')
api.add_resource(TaskAPI, '/playqr/api/v1.0/task/<int:id>', endpoint='task')


class Image(db.Model):
    __tablename__ = 'images'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    image_data = db.Column(db.String(240), index=True)
    decode_data = db.Column(db.String(240))
    take_time = db.Column(db.DateTime)
    position = db.Column(db.String(240))

    user_id = db.Column(db.Integer, db.ForeignKey('users.id'))
    user = db.relationship('User', backref=db.backref('images', lazy='dynamic'))

    def __init__(self, image_data, decode_data, take_time, position, user):
        self.image_data = image_data
        self.decode_data = decode_data
        self.take_time = take_time
        self.position = position
        self.user = user

    def __repr__(self):
        return '<Image %r>' % self.image_data


class User(db.Model):
    __tablename__ = 'users'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    username = db.Column(db.String(80), unique=True, index=True, nullable=False)
    password = db.Column(db.String(120), nullable=False)

    def __init__(self, username, password):
        self.username = username
        self.password = password

    def __repr__(self):
        return '<User %r>' % self.username


@app.route('/')
def hello():
    return redirect(url_for('index', username='index.html'))


@app.route('/<username>')
def index(username):
    if username == 'index.html':
        username = 'World'
    return render_template('index.html', username=username)


@app.route('/login', methods=['GET', 'POST'])
def login():
    form = LoginForm()
    if form.validate_on_submit():
        username = form.username.data
        user = User.query.filter_by(username=username).first
        if user:
            return redirect(url_for('index', username=username))
    return render_template('login.html', form=form)


@app.route('/register', methods=['GET', 'POST'])
def register():
    form = RegisterForm()
    if form.validate_on_submit():
        username = form.username.data
        password = form.password.data
        user = User(username, password)
        db.session.add(user)
        db.session.commit()
        return redirect(url_for('login', username=username))
    return render_template('register.html', form=form)


if __name__ == '__main__':
    app.run(debug=True)
