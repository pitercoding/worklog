// Karma configuration. Only needed to add a CI-friendly Chrome launcher;
// everything else follows the Angular defaults.
module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage'),
    ],
    reporters: ['progress', 'kjhtml'],
    browsers: ['Chrome'],
    customLaunchers: {
      // Headless Chrome without the sandbox: required on most CI runners (containers, restricted user namespaces).
      ChromeHeadlessCI: {
        base: 'ChromeHeadless',
        flags: ['--no-sandbox'],
      },
    },
    restartOnFileChange: true,
  });
};
