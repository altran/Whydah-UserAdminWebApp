UseradminApp.controller('RoleCtrl', function($scope, Users) {

  $scope.role = {
    applicationRoleName: "developer",
    applicationRoleValue: "20",
    applicationId: 3,
    applicationName:"Whydah",
    organizationId: 1,
    organizationName:"Whydah"
  }

  $scope.roleProperties = [
    {value: 'applicationRoleName', minLength: 2, maxLength: 64, type: 'text'},
    {value: 'applicationRoleValue', minLength: 0, maxLength: 128, type: 'text'},
    {value: 'applicationId', minLength: 1, maxLength: 48, type: 'text'},
    {value: 'organizationId', minLength: 1, maxLength: 48, type: 'text'}
  ];

  $scope.addRole = function() {
    console.log('Adding role for user with uid:', Users.user.uid);
    Users.addRoleForCurrentUser($scope.role);
  }

});