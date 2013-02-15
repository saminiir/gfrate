'''
Created on Feb 15, 2013

@author: sailniir
'''
import datetime
from sqlalchemy import schema, types, orm

metadata = schema.MetaData()

def now():
    return datetime.datetime.now()

user_table = schema.Table('user', metadata,
    schema.Column('id', types.Integer, 
    schema.Sequence('user_seq_id', optional=True), primary_key=True),
    schema.Column('name', types.Text(), nullable=False),
    schema.Column('password', types.Text(), nullable=False),
    schema.Column('registerdate', types.TIMESTAMP(), nullable=False)
)

class User(object):
    pass

orm.mapper(User, user_table)
