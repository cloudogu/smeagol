'use strict';
const fs = require('fs');
const paths = require('../config/paths');


// Do this as the first thing so that any code reading it knows the right env.
process.env.BABEL_ENV = 'test';
process.env.NODE_ENV = 'test';
process.env.PUBLIC_URL = '';

// Makes the script crash on unhandled rejections instead of silently
// ignoring them. In the future, promise rejections that are not handled will
// terminate the Node.js process with a non-zero exit code.
process.on('unhandledRejection', err => {
  throw err;
});

// Ensure environment variables are read.
require('../config/env');

const jest = require('jest');
const argv = process.argv.slice(2);


// use junit reports for ci builds
// https://github.com/michaelleeallen/jest-junit-reporter
if (process.env.CI) {
  if (!fs.existsSync(paths.testsReportDir)) {
    fs.mkdirSync(paths.testsReportDir);
  }
  process.env.TEST_REPORT_FILENAME = paths.testsReportFilename;
  process.env.TEST_REPORT_PATH = paths.testsReportDir;
  argv.push('--testResultsProcessor=./node_modules/jest-junit-reporter');
}

// Watch unless on CI or in coverage mode
if (!process.env.CI && argv.indexOf('--coverage') < 0) {
  argv.push('--watch');
}


jest.run(argv);
