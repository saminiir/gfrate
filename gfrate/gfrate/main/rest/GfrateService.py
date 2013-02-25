'''
Created on Feb 5, 2013

@author: sailniir
'''

import sqlite3
from contextlib import closing
from gfrate.main import app
from flask.globals import request
from flask.wrappers import Response

# configuration
DATABASE = '/tmp/gfrate_test.db'
DEBUG = True
SECRET_KEY = 'development key'
USERNAME = 'admin'
PASSWORD = 'default'

@app.route('/')
def index():
    return 'Here be index!'

@app.route('/initiate', methods=['GET'])
def initiate():
    #   r = requests.post("lol")
    
    oauth_consumer_key = request.args.get('oauth_consumer_key', '')
    oauth_signature_method = request.args.get('oauth_signature_method', '')
    oauth_timestamp = request.args.get('oauth_timestamp', '')
    oauth_nonce = request.args.get('oauth_nonce', '')
    test = request.args.get('test', '')
    
    resp = Response(status=200)
    resp.headers['test'] = ("testing", "ja")
    #resp.data = "testasdffsdasf"
    
    return resp
   
     

@app.route('/hello/<name>')
def hello(name):
    return 'Hello {0}'.format(name)

def connect_db():
    return sqlite3.connect(app.config['DATABASE'])

def init_db():
    with closing(connect_db()) as db:
        with app.open_resource('schema.sql') as f:
            db.cursor().executescript(f.read())
        db.commit()
