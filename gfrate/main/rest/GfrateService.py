'''
Created on Feb 5, 2013

@author: sailniir
'''

import sqlite3
from flask import Flask
from contextlib import closing

# configuration
DATABASE = '/tmp/gfrate_test.db'
DEBUG = True
SECRET_KEY = 'development key'
USERNAME = 'admin'
PASSWORD = 'default'

app = Flask(__name__)
app.config.from_object(__name__)

@app.route('/')
def index():
    return 'Here be index!'

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

if __name__ == '__main__':
    app.run(debug=True)