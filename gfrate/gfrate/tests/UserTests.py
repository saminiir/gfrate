'''
Created on Feb 15, 2013

@author: sailniir
'''
import unittest
from sqlalchemy.engine import create_engine
from gfrate.main.models import Model
from sqlalchemy import orm
import datetime
import contextlib

class Test(unittest.TestCase):

    def setUp(self):
        engine = create_engine('sqlite:///:memory', echo=True)

        Model.metadata.bind = engine
        Model.metadata.create_all()
        
        with contextlib.closing(engine.connect()) as con:
            trans = con.begin()
            for table in reversed(Model.metadata.sorted_tables):
                con.execute(table.delete())
                trans.commit()
 
        
        sm = orm.sessionmaker(bind=engine, autoflush=True, autocommit=False,
                              expire_on_commit=True)
        self.session = orm.scoped_session(sm)
    
    def tearDown(self):
        self.session.remove()

    def testSQLAlchemyForUser(self):
        user = self.createUser("sailniir", "secret", "")
        
        self.assertEqual(user.name, "sailniir")
        self.assertEqual(user.password, "secret")
        self.assertEqual(user.registerdate, "")

    def testUserSaveToDb(self):
        user = self.createUser("sailniir", "secret", datetime.datetime.now());
        
        user_q = self.session.query(Model.User)
        self.assertEquals(len(user_q.all()), 0)
        
        self.session.add(user)
        self.session.flush()
        self.session.commit()
        
        self.assertEquals(len(user_q.all()), 1)

    def createUser(self, name, password, registerdate):
        user = Model.User()
        user.name = name
        user.password = password
        user.registerdate = registerdate
        
        return user
    
if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()