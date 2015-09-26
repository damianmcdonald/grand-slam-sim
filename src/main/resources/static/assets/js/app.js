'use strict';

/* App Module */

var grandSlamSimApp = angular.module('grandSlamSimApp', [
														'ngRoute',
														'ui.bootstrap',
														'dndLists',
														'toaster'
													 ]);
													 
grandSlamSimApp.config(['$routeProvider', function($routeProvider) {
  $routeProvider.
  when('/draw/:tournamentName', {
    templateUrl: 'assets/partials/draw.html',
    controller: 'TournamentController'
  }).
  when('/admin', {
    templateUrl: 'assets/partials/admin.html',
    controller: 'AdminController'
  }).
  when('/select', {
    templateUrl: 'assets/partials/select.html',
    controller: 'SelectController'
  }).
  otherwise({
    redirectTo: '/select'
  });
}]);