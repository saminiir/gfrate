'''
Created on Feb 18, 2013

@author: sailniir
'''
import unittest
from gfrate.main.rest import GfrateService

class OAuthTest(unittest.TestCase):
    
    def setUp(self):
        self.app = GfrateService.app.test_client()
        
        pass
    
    def testInitiate(self):
        rv = self.app.post('/initiate', data=dict(
                        oauth_consumer_key="dpf43f3p2l4k3l03",
                        oauth_timestamp="137131200",
                        oauth_nonce="wIjqoS",
                        oauth_callback="http%3A%2F%2Fprinter.example.com%2Fready",
                        oauth_signature="74KNZJeDHnMBp0EMJ9ZHt%2FXKycU%3D"
                        ))
        
        #self.assertEquals(rv.data, 'test')
        
        assert 'oauth_token' in rv.data
    
    def initiateOAuth(self):
        rv = self.app.get('\initiate')
        return rv