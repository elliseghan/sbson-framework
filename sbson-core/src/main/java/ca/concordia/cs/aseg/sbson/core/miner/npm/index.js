const ChangesStream = require('changes-stream');
const Request = require('request');
const db = 'https://replicate.npmjs.com';
const log4js = require('log4js');
const Normalize = require('normalize-registry-metadata');

log4js.configure({
    appenders: { registry: { type: 'file', filename: 'registry.log' } },
    categories: { default: { appenders: ['registry'], level: 'info' } }

});

var changes = new ChangesStream({
    db: db,
    include_docs: true
});

var logger = log4js.getLogger('registry');

Request.get(db, function (err, req, body) {        // <- make a request to the db
    var end_sequence = JSON.parse(body).update_seq; // <- grab the update_seq value
    changes.on('data', function (change) {
        if (change.seq >= end_sequence) {             // <- if we're at the last change
            process.exit(0);                            // <- end the program successfully ("0")
        }
        //console.log(change.doc);
        logger.info(JSON.stringify (change.doc,null,' '));
    })
});