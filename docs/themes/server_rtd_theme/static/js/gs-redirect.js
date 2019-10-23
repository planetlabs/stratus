
//Redirect to the relative path "suffix" within the geoserver user manual.
window.pathToGeoServerDocs = function(suffix) {
  var gsRoot = $(".gs_logo > a ").attr("href");
  gsRoot = gsRoot.replace("index.html", "");
  gsRoot = gsRoot.replace("#", "");

  gsRoot = gsRoot + suffix;

  window.location.href = gsRoot;
}

window.pathToServerDocs = function(suffix) {
  var stratusRoot = $(".rst-other-versions > .versions-footer > a ").attr("href");
  stratusRoot = stratusRoot.replace("index.html", "");
  stratusRoot = stratusRoot.replace("#", "");

  stratusRoot = stratusRoot + suffix;

  window.location.href = stratusRoot;
}