requirejs.config({
  //By default load any module IDs from js/lib
  baseUrl: '../assets/js/',
  //except, if the module ID starts with "app",
  //load it from the js/app directory. paths
  //config is relative to the baseUrl, and
  //never includes a ".js" extension since
  //the paths config could be for a directory.
  paths: {
    viewTpl: '../template'
  },

  map: {
    '*': {
      'css': '../fh-ui/js/lib/require/css.min',
      'text': '../fh-ui/js/lib/require/text.js',
      'template': '../fh-ui/js/lib/template/template.js',
      'model': "../assets/js/common/model.js",
      'data': "../assets/js/common/data.js",
      'tool': "../assets/js/common/tool.js",
      'public': "../assets/js/common/public.js",
      'filter': "../assets/js/common/filter.js",
      'rightModal': "../assets/js/common/rightModal.js",
      'header': "../assets/js/header/index.js"
    }
  },

  shim: {
    'jquery': {
      exports: 'jQuery'
    }
  }
});
