from flask.ext.restful import Resource, reqparse, fields, marshal
from flask.ext.httpauth import HTTPBasicAuth

auth = HTTPBasicAuth()


@auth.get_password
def get_password(username):
    pass


@auth.error_handler
def error_handler():
    pass


class TasksAPI(Resource):
    def __init__(self):
        super(TasksAPI, self).__init__()

    def get(self):
        return 'TasksAPI GET'

    def post(self):
        pass


class TaskAPI(Resource):
    def __init__(self):
        super(TaskAPI, self).__init__()

    def get(self):
        pass

    def put(self):
        pass

    def delete(self):
        pass


