var assert = require("assert")
describe('Array', function(){
  describe('#indexOf()', function(){
    it('should return -1 when the value is not present', function(){
      assert.equal(-1, [1,2,3].indexOf(5));
      assert.equal(-1, [1,2,3].indexOf(0));
    })
  })
})

require("should");

describe('feature', function() {
  it("should add two numbers", function() {
    (2+2).should.equal(4);
  });
});

routes = require("../routes");

describe('routes', function(){
  describe('index', function(){
    it("should display index with title", function(){
      var req = null;
      var res = {
        render: function(view, vars){
          view.should.equal("index");
          vars.title.should.equal("Express");
        }}; 
      routes.index(req, res);
    });
  });

  describe('login', function(){
    it("should display login asadsds", function(){
      var req = null;
      var res = {
        render: function(view, vars){
          view.should.equal("login");
          vars.title.should.equal("Login");
        }
      }; 
      routes.loginForm(req, res);
    });
  });
});
