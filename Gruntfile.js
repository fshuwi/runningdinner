var argv = require( 'optimist' ).argv,
  _ = require( 'underscore' );


module.exports = function ( grunt ) {
  grunt.initConfig( {
    pkg: grunt.file.readJSON( "package.json" ),
    revision: {
      number: (new Date()).getTime()
    },

    vendorJsDir: 'src/main/webapp/resources/js',
    targetDir: 'src/main/webapp/resources/js/distnew',//(argv.target || 'target'),
    
    srcCssDir: 'src/main/webapp/resources/css',
    targetCssDir: 'src/main/webapp/resources/css/dist',
    
    srcDir: 'src/main/webapp',

    clean: {
      tmp: 'tmp',
      target: '<%= targetDir %>',
      targetCss : '<%= targetCssDir %>',
    },

    concat: {
      options: {
        // add comment about where this src comes from
        // this is applied to all concat tasks, so make sure the comment
        // works in them or overwrite it.
        process: function ( src, filepath ) {
          return "\n/* Original File: " + filepath + "*/\n" + src;
        }
      },

      'deps.js': {
        src: [
          '<%= vendorJsDir %>/jquery-1.11.1.min.js',
          '<%= vendorJsDir %>/bootstrap.min.js',
          '<%= vendorJsDir %>/jquery-ui-1.10.4.custom.min.js',
          '<%= vendorJsDir %>/jquery.ui.datepicker-de.js',
          //'<%= vendorJsDir %>/toastr.min.js',
          '<%= vendorJsDir %>/jquery.tooltipster.min.js',
          '<%= vendorJsDir %>/validator.min.js'
        ],
        dest: '<%= targetDir %>/deps.js'
      },

      'app.js': {
        src: [
          '<%= vendorJsDir %>/common.js'
        ],
        dest: '<%= targetDir %>/app.js'
      },
      
      'app.css': {
	src: [
	      '<%= srcCssDir %>/tooltipster.css',
	      '<%= srcCssDir %>/bootstrap.min.css',
	      '<%= srcCssDir %>/toastr.min.css',
	      '<%= srcCssDir %>/bootstrap-theme.min.css',
	      '<%= srcCssDir %>/flick/jquery-ui-1.10.4.custom.min.css',
	      '<%= srcCssDir %>/themes/tooltipster-shadow.css',			
	      '<%= srcCssDir %>/custom.css'
	],
	dest: '<%= targetCssDir %>/app.css'
      }
      
    },

    replace: {
      buildno: {
        files: [
          {
            expand: true,
            src: '**/*.{js,css,html,jsp}',
            cwd: '<%= targetDir %>/',
            dest: '<%= targetDir %>'
          }
        ],
        options: {
          prefix: '',
          variables: {
            '@@buildno@@': '<%= revision.number %>'
          }
        }
      }
    },

    copy: {

      app: {
        expand: true,
        cwd: '<%= srcDir %>',
        src: ['**/*', '!**/*.{js,css,less}'],
        filter: 'isFile',
        dest: '<%= targetDir %>'
      }
    },

    cssmin: {
      all: {
        expand: true,
        cwd: '<%= targetCssDir %>',
        src: ['**/*.css'],
        dest: '<%= targetCssDir %>'
      }
    },

    watch: {
      options: {
        // spawn disabled so that jshint can hint files individually,
        // see https://github.com/gruntjs/grunt-contrib-watch#compiling-files-as-needed
        spawn: false,
        livereload: true
      },

      all: {
         files: ['<%= srcDir %>/**/*.*'],
         tasks: ['jshint', 'build-dev']
      }
    },

    jshint: {
      options: {
        jshintrc: '.jshintrc',
        force: false  // fail on errors
      },

      src: ['<%= vendorJsDir %>/common.js']
    },

    karma: {
      unit: {
        configFile: 'karma.conf.js'
      }
    }
  } );

  require( 'load-grunt-tasks' )( grunt );
  grunt.loadTasks( 'tasks' );
  grunt.loadNpmTasks('grunt-karma');

  // jshint only the (js) files that were changed
  grunt.event.on( 'watch', function ( action, filepath ) {
    var filepaths = _.flatten( [filepath] )
    filepaths = _.filter( filepaths, function ( path ) {
      return path.match(/\.js$/)
    } );
    
    grunt.config( 'jshint.src', filepaths )
  } );

  grunt.registerTask( 'build-dev', [/*'jshint', 'copy',*/ 'concat'] );
  grunt.registerTask( 'build', ['clean', 'build-dev', 'replace:buildno', 'cssmin'] );
  grunt.registerTask( 'default', ['clean', 'build-dev', 'watch'] );
  grunt.registerTask('test', ['karma:unit']);
};