var should = require('should');
var request = require('superagent');
var app = require('../app');

describe('oauth', function() {
  describe('POST /oauth/request_token', function(){ 	
    it('should work', function() {
      [1,2,3].indexOf(4).should.equal(-1);
    }); 
  });
});
