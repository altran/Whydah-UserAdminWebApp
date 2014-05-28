UseradminApp.controller('RoleCtrl', function($scope, Users) {

  $scope.role = {
    applicationRoleName: "developer",
    applicationRoleValue: "20",
    applicationId: "3",
    applicationName:"Whydah",
    organizationId: "1",
    organizationName:"Whydah"
  }

  $scope.roleProperties = [
    {value: 'applicationRoleName', minLength: 2, maxLength: 64, type: 'text'},
    {value: 'applicationRoleValue', minLength: 0, maxLength: 128, type: 'text'},
    {value: 'applicationId', minLength: 1, maxLength: 64, type: 'text'},
    {value: 'applicationName', minLength: 1, maxLength: 64, type: 'text'},
    {value: 'organizationId', minLength: 1, maxLength: 64, type: 'text'},
    {value: 'organizationName', minLength: 1, maxLength: 64, type: 'text'}
  ];

  $scope.dict = {
    en: {
      applicationName: 'Application',
      applicationId: 'Application Id',
      organizationName: 'Organization',
      organizationId: 'Organization Id',
      applicationRoleName: 'Role',
      applicationRoleValue: 'Value'
    }
  }

  $scope.addRole = function() {
    if($scope.addRoleForMultiple) {
        Users.addRoleForSelectedUsers($scope.role);
    } else {
        Users.addRoleForCurrentUser($scope.role);
    }
  }

});