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
        rv = self.app.get("""/initiate?oauth_version=1.0&oauth_nonce=67c70a6035012347d9998308c2f3af04
                          &oauth_timestamp=1361727573&oauth_consumer_key=test&oauth_signature_method=HMAC-SHA1
                          &oauth_signature=9j07IVLp3nyl2fU6ez5FfkTGtT0%3D""")
        
        #self.assertEquals(rv.data, 'test')
        
        assert 'oauth_token' in rv.data
    
    def initiateOAuth(self):
        rv = self.app.get('/initiate')
        return rv