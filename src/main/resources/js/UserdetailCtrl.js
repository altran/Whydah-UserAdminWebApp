UseradminApp.controller('UserdetailCtrl', function($scope, $http, $routeParams, Users) {
  
  $scope.session.activeTab = 'user';

  $scope.editableUserProperties = [
    'firstName',
    'lastName',
    'email',
    'cellPhone'
  ];

  $scope.editableRoleProperties = [
    'applicationName',
    'organizationName',
    'roleName',
    'roleValue'
  ];

  $scope.dict = {
    en: {
      firstName: 'First name',
      lastName: 'Last name',
      email: 'E-mail',
      cellPhone: 'Cellphone',
      applicationName: 'Application',
      organizationName: 'Organization',
      roleName: 'Role',
      roleValue: 'Value'
    }
  } 

  var noRolesSelectedMessage = 'Please select a role first!';
  $scope.rolesRequiredMessage = noRolesSelectedMessage;

  $scope.$watch('usersSelected', function(){
    $scope.rolesRequiredMessage = ($scope.rolesSelected) ? '' : noRolesSelectedMessage;
  });

  $scope.save = function() {
    // Make sure these $scope-values are properly connected to the view
    if(Users.user.isNew) {
        Users.add(Users.user);
        Users.user.isNew = false;
    } else {
        Users.save(Users.user);
    }
  }

});