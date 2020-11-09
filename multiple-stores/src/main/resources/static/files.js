angular.module('filesApp', [])
  .controller('FilesListController', function($http) {
    var filesList = this;
    filesList.files = []; 
    
    filesList.getFilesList = function() {
        $http.get('/files/').
            success(function(data, status, headers, config) {
                if (data._embedded != undefined) {
        			filesList.files = [];
                    angular.forEach(data._embedded.files, function(file) {
                        filesList.files.push(file);
	                });
	            }
	        });
	    };
    filesList.getFilesList(); 
    
    filesList.getHref = function(file) {
    	return file._links["self"].href
    };

    filesList.upload = function() {
    	var f = document.getElementById('file').files[0];
    	var file = {name: f.name, summary: filesList.summary};
    	
    	$http.post('/files/', file).
    		then(function(response) {
	    		var fd = new FormData();
	            fd.append('file', f);
	            return $http.put(response.headers("Location"), fd, {
	                transformRequest: angular.identity,
	                headers: {'Content-Type': undefined}
	            });
	        })
	        .then(function(response) {
    			filesList.title = "";
    			filesList.keywords = "";
    			filesList.getFilesList();
    			document.getElementById('file').files[0] = undefined;
	        });
    }
  });