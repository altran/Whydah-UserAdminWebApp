UseradminApp.directive('uaModal', function() {
  return {
    restrict: 'A',
    scope: { uaModalName: '=', uaModalController: '=' },
    templateUrl: 'template/modal.html',
    controller: function($scope) {
    	console.log('Modal', $scope.uaModalController);
    }
  };
});