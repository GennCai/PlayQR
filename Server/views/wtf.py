#coding:utf-8
from flask.ext.wtf import Form
from wtforms import StringField, PasswordField, FileField, SubmitField
from wtforms.validators import DataRequired, Length


class RegisterForm(Form):
    username = StringField('Username', validators=[DataRequired()])
    password = PasswordField('Password', validators=[DataRequired(), Length(6)])
    re_password = PasswordField('Repeat Password')
    submit = SubmitField()


class LoginForm(Form):
    username = StringField('Username', validators=[DataRequired()])
    password = PasswordField('Password', validators=[DataRequired(), Length(6)])
    login = SubmitField()


class TakesPostForm(Form):
    image_data = FileField('image_data', validators=[DataRequired()])
    decode_data = StringField(validators=[DataRequired()])
    time = StringField()
    location = StringField()


class UserEditForm(Form):
    username = StringField('Username', validators=[DataRequired()])
    password = PasswordField('Password', validators=[DataRequired(), Length(6)])
    upload_folder = StringField('Upload_folder')
    submit = SubmitField()


