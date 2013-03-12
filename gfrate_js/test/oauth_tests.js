var should = require('should');
var request = require('supertest');
var app = require('../app');
	
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
      var consumer_key = 'abc123';
      var consumerSecret = 'ssh-secret';
      var oauth_header = 'OAuth oauth_nonce="K7ny27JTpKVsTgdyLdDfmQQWVLERj2zAK5BslRsqyw", oauth_callback="http%3A%2F%2Fmyapp.com%3A3005%2Ftwitter%2Fprocess_callback", oauth_signature_method="HMAC-SHA1", oauth_timestamp="'+timestamp+'", oauth_consumer_key="'+consumer_key+'", oauth_signature="Pc%2BMLdv028fxCErFyi8KXFM%2BddU%3D", oauth_version="1.0"';
 
      request(app)
        .post('/oauth/request_token')
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
