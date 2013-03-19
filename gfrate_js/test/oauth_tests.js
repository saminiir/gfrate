var should = require('should');
var request = require('supertest');
var app = require('../app');
var crypto = require('crypto');
var querystring = require('querystring');
	
describe('oauth', function() {

  var oauth_token = "";
  var oauth_token_secret = "";

  describe('POST /oauth/request_token', function(){ 	
    it('should respond 401 Unauthorized without params', function(done){
      request(app)
        .post('/oauth/request_token')
        .send({ test: 'test' })
        .expect(401, done);
    });

    it('should respond with token and secret with valid params', function(done){
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
      console.log('========SIGNATURE\n'+signature_raw);
     
      var oauth_header = 'OAuth oauth_consumer_key="'+consumer_key+'", oauth_nonce="'+nonce+'", oauth_signature_method="'+signature_method+'", oauth_timestamp="'+timestamp+'",  oauth_signature="'+signature+'", oauth_version="1.0"';
 
      request(app)
        .post('/oauth/request_token')
        .set('Content-Type', 'Content-Type: application/x-www-form-urlencoded')
        .set('Authorization', oauth_header)        
        .end(function(err, res) {
          console.log(res.header);  
          console.log(res.body);
          console.log(res.text);
          var parsed = querystring.parse(res.text); 
          oauth_token = parsed['oauth_token'];
          oauth_token_secret = parsed['oauth_token_secret'];
          res.text.should.include('oauth_token'); 
          res.text.should.include('oauth_token_secret'); 
          done();
        });
    });

    it('authorize should redirect to login', function(done) {
      request(app)
        .get('/authorize?oauth_token='+oauth_token)
        .end(function(err, res) {
          res.header['location'].should.equal('/login');
          res.redirect.should.equal(true);
          console.log(res.header);
          console.log(res.body);
          console.log(res.text);
          console.log(res.redirect);
          done();
        });
    });

    it('should pass login', function(done){
      console.log("teest");
      done();
    });
  });

  describe('authorize', function() {
    console.log("oauthorize log");
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
