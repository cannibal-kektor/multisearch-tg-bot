//db.getSiblingDB('admin').auth(
//    process.env.MONGO_INITDB_ROOT_USERNAME,
//    process.env.MONGO_INITDB_ROOT_PASSWORD
//);
db.getSiblingDB('$external').createUser({
    user: "CN=telegramApp",
    roles: [
         { role: "root", db: "admin" }
    ]
});
db.getSiblingDB('$external').createUser({
    user: "CN=elasticAgent",
    roles: [
         { role: "clusterMonitor", db: "admin"  }
    ]
});