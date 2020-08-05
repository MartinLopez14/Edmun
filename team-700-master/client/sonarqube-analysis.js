const sonarqubeScanner =  require('sonarqube-scanner');
sonarqubeScanner(
    {
        serverUrl:  'https://csse-s302g7.canterbury.ac.nz/sonarqube/',
        token: "3dc052fa137c831175f0212b5cf6dec7ea2412d1",
        options : {
            'sonar.projectKey': 'team-700-client',
            'sonar.projectName': 'Team 700 - Client',
            "sonar.sourceEncoding": "UTF-8",
            'sonar.sources': 'src',
            'sonar.tests': 'src/test',
            'sonar.inclusions': '**',
            'sonar.test.inclusions': 'src/**/*.spec.js,src/**/*.test.js,src/**/*.test.ts, src/test',
            'sonar.typescript.lcov.reportPaths': 'coverage/lcov.info',
            'sonar.javascript.lcov.reportPaths': 'coverage/lcov.info',
            'sonar.testExecutionReportPaths': 'coverage/test-reporter.xml'
        }
    }, () => {});