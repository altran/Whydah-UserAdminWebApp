UseradminApp.controller('RoleCtrl', function($scope, Users) {

  $scope.role = {
    applicationRoleName: '',
    applicationRoleValue: '',
    applicationId: '',
    applicationName: '',
    organizationName: ''
  }

  $scope.applications = [
    {
        applicationName: 'Whydah',
        applicationId: 3,
        organizations: [
            'Developer',
            'Tester',
            'Designer',
            'Sales'
        ]
    },
    {
        applicationName: 'ACS',
        applicationId: 4,
        organizations: [
            'Oslo',
            'Stockholm',
            'Gothenburg'
        ]
    }
  ]

  $scope.currentApplication = {};

  $scope.dict = {
    en: {
      application: 'Application',
      organization: 'Organization',
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