var should = require('should');
var request = require('supertest');
var app = require('../app');
var crypto = require('crypto');
	
describe('oauth', function() {
  describe('POST /oauth/request_token', function(){ 	
    it('should work', function() {
      [1,2,3].indexOf(4).should.equal(-1);
    }); 

    it('should respond 401 Unauthorized without params', function(done){
      request(app)
        .post('/oauth/request_token')
        .send({ test: 'test' })
        .expect(401, done);
    });

    it('should respond OK with params', function(done){
      var timestamp = Math.floor(new Date().getTime() / 1000);

      var base_string_uri = 'http://127.0.0.1:3457/oauth/request_token';
      var encoded_base_uri = encodeURIComponent(base_string_uri);
      console.log(encoded_base_uri);

      var consumer_key = 'abc123';
      var consumerSecret = 'ssh-secret';
      var nonce = "testnonce";
      var signature_method = 'HMAC-SHA1';

      var request_params = 'oauth_consumer_key='+consumer_key+'&oauth_nonce='+nonce+'&oauth_signature_method='+signature_method+'&oauth_timestamp='+timestamp + "&oauth_version=1.0";
      
      console.log(request_params);

      var hash_key = consumerSecret + "&";
      var signature_plaintext = ""+consumerSecret+"%26";

      var signature_raw = 'POST&'+encoded_base_uri+'&'+encodeURIComponent(request_params);
      var signature = crypto.createHmac('sha1', hash_key).update(signature_raw).digest('base64');
      console.log("Encrypted signature: " + signature);
     // var signature = 'c0d3edb74acf155cef0e671e2c0b5f9def71d337'; 
      console.log('========SIGNATURE\n'+signature_raw);
     
      var oauth_header = 'OAuth oauth_consumer_key="'+consumer_key+'", oauth_nonce="'+nonce+'", oauth_signature_method="'+signature_method+'", oauth_timestamp="'+timestamp+'",  oauth_signature="'+signature+'", oauth_version="1.0"';

      //var oauth_header = 'OAuth oauth_nonce="K7ny27JTpKVsTgdyLdDfmQQWVLERj2zAK5BslRsqyw", oauth_callback="http%3A%2F%2Fmyapp.com%3A3005%2Ftwitter%2Fprocess_callback", oauth_signature_method="HMAC-SHA1", oauth_timestamp="'+timestamp+'", oauth_consumer_key="'+consumer_key+'", oauth_signature="Pc%2BMLdv028fxCErFyi8KXFM%2BddU%3D", oauth_version="1.0"';
 
      request(app)
        .post('/oauth/request_token')
        .set('Content-Type', 'Content-Type: application/x-www-form-urlencoded')
        .set('Authorization', oauth_header)        
        .end(function(err, res) {
          console.log(res.header);  
          console.log(res.body);
          done();
        });
    });
  });
});

describe('test', function() {
  it('should show login', function(done) {
    request(app)
      .get('/login')
      .end(function(err, res) {
        res.should.have.status(200);
        res.text.should.include('Login');

        done();
      });
  });
});
