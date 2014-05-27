UseradminApp.controller('UserCtrl', function($scope, $http, $routeParams, Users, Applications) {

  $scope.session.activeTab = 'user';

  $scope.users = Users;
  $scope.applications = Applications;

  $scope.orderByColumn = 'username';
  $scope.orderReverse = false;

  var noUsersSelectedMessage = 'Please select a user first!';
  Users.requiredMessage = noUsersSelectedMessage;

  $scope.$watch('users.selected', function(){
    Users.requiredMessage = (Users.selected) ? '' : noUsersSelectedMessage;
  });
  
  $scope.loadApplications 

  $scope.searchUsers = function() {
    Users.search($scope.searchQuery);
  }

  $scope.clearAllApps = function() {
    console.log('Clear all');
    angular.forEach( Users.applications, function(el, index) {
      el.isSelected = false;
    });
  }

  $scope.getUserByUsername = function(username, callback) {
    console.log('Getting user by username:', username);
    $http({
      method: 'GET',
      url: myHostJsonUsers+username
    }).success(function(data){
      callback(data);
    });
  }

  $scope.activateUserDetail = function(username) {
    console.log('Activating user detail...', username);
    Users.get(username, function(){
      Users.getRolesForCurrentUser( function(){
        $('#userdetail').modal('show');
      });
    });
  }

  $scope.newUserDetail = function() {
    Users.user = {isNew: true};
    Users.userRoles = {};
    $('#userdetail').modal('show');
  }

  $scope.addRoleForUsers = function() {
    console.log('Adding roles for users...');
    $('#addrole').modal('show');
  }

  $scope.changeOrder = function(orderByColumn) {
    $scope.orderByColumn = orderByColumn;
    $scope.orderReverse = !$scope.orderReverse;
  }

  function init() {
    Users.search();
    Applications.search();
    // Don't hide application-filter menu when clicking an option
    $('.dropdown-menu').click(function(e) {
      e.stopPropagation();
    });
  }

  if(Users.list.length<1) {
    init();
  }

});