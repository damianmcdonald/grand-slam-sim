'use strict';

var grandSlamSimApp = angular.module('grandSlamSimApp', [
														'ngRoute',
														'ui.bootstrap',
														'dndLists',
														'toaster'
													 ]);
													 
grandSlamSimApp.config(['$routeProvider', function($routeProvider) {
  $routeProvider.
  when('/tournament/:tournamentName', {
    templateUrl: 'assets/partials/tournament.html',
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