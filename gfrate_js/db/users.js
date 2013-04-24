var users = [
    { id: '1', username: 'bob', password: 'secret', name: 'Bob Smith', points: 55 },
    { id: '2', username: 'joe', password: 'password', name: 'Joe Davis', points: -47 },
    { id: '3', username: 'pekka', password: 'testi', name: 'Pekka Martikainen', points: 67 }
];


exports.find = function(id, done) {
  for (var i = 0, len = users.length; i < len; i++) {
    var user = users[i];
    if (user.id === id) {
      return done(null, user);
    }
  }
  return done(null, null);
};

exports.findByUsername = function(username, done) {
  for (var i = 0, len = users.length; i < len; i++) {
    var user = users[i];
    if (user.username === username) {
      return done(null, user);
    }
  }
  return done(null, null);
};

exports.findAll = function() {

    element = null;
    topList = [];

    for (var i = 0; i < users.length; i++) {
        element = { points: users[i].points, name: users[i].name };
        topList.push(element);
    }

    topList.sort(pointsCompare);

    return topList;
};

function pointsCompare(a,b) {
  if (a.points > b.points)
     return -1;
  if (a.points < b.points)
    return 1;
  return 0;
}