UseradminApp.controller('UserdetailCtrl', function($scope, Users) {

  $scope.userProperties = [
    {value: 'firstName', minLength: 2, maxLength: 64, type: 'text'},
    {value: 'lastName', minLength: 2, maxLength: 64, type: 'text'},
    {value: 'email', minLength: 4, maxLength: 64, type: 'email'},
    {value: 'cellPhone', minLength: 3, maxLength: 48, type: 'text'}
  ];

  $scope.editableRoleProperties = [
    'applicationName',
    'organizationName',
    'applicationRoleName',
    'applicationRoleValue'
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
        delete Users.user.isNew;
        Users.add(Users.user);
    } else {
        Users.save(Users.user);
    }
  }

  $scope.delete = function() {
    if(window.confirm('Are you sure you want to delete?')) {
        console.log('Deleting user', Users.user.username);
        Users.delete(Users.user);
    } else {
        console.log('Cancelled deletion of user', Users.user.username);
    }
  }

});