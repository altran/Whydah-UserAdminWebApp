UseradminApp.controller('ApplicationCtrl', function($scope, Applications) {

  $scope.session.activeTab = 'application';

  $scope.applications = Applications;

  $scope.searchApps = function() {
  	Applications.search($scope.searchQuery);
  }

  function init() {
    Applications.search();
  }

  if(Applications.list.length<1) {
    init();
  }

  $scope.activateApplicationDetail = function(applicationName) {
    console.log('Activating application detail...', applicationName);
    Applications.get(applicationName, function(){
        $scope.form.applicationDetail.$setPristine();
        $('#applicationdetail').modal('show');
    });
  }

});