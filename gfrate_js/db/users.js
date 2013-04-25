var users = [
    { id: '1', username: 'bob', password: 'secret', name: 'Bob Smith', points: 55 },
    { id: '2', username: 'joe', password: 'password', name: 'Joe Davis', points: -47 },
    { id: '3', username: 'pekka2', password: 'testi', name: 'Pekka Martikainen', points: 67 },
    { id: '4', username: 'bob3', password: 'secret', name: 'Testi', points: 155 },
    { id: '5', username: 'joe4', password: 'password', name: 'Ilias Anatolian', points: -23 },
    { id: '6', username: 'pekka5', password: 'testi', name: 'Keke Rosberg', points: 6 },
    { id: '7', username: 'bob6', password: 'secret', name: 'Antti-Einari', points: 5 },
    { id: '8', username: 'joe7', password: 'password', name: 'Testaaja', points: -8 },
    { id: '9', username: 'pekka8', password: 'testi', name: 'Pekka', points: 16 },
    { id: '10', username: 'bob9', password: 'secret', name: 'Anders Smith', points: 24 },
    { id: '11', username: 'joe10', password: 'password', name: 'John Doe', points: -10 },
    { id: '12', username: 'pekka11', password: 'testi', name: 'Jane Doe', points: 94 }
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

    return topList.slice(0,10);
};

function pointsCompare(a,b) {
  if (a.points > b.points)
     return -1;
  if (a.points < b.points)
    return 1;
  return 0;
}