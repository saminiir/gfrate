var assert = require("assert")
require("should");
var routes = require("../routes");

describe('routes', function(){
  describe('index', function(){
    it("should display index with title", function(){
      var req = null;
      var res = {
        render: function(view, vars){
          view.should.equal("index");
          vars.title.should.equal("RateMyPartner");
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
