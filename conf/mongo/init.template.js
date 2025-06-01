db.getSiblingDB("admin").createUser({
    user: "ADMIN_USERNAME",
    pwd: "ADMIN_PASSWORD",
    roles: [
        { role: "root", db: "admin" }
    ]
});
db.getSiblingDB('$external').createUser({
    user: "CN=APP_USERNAME",
    roles: [
         { role: "root", db: "admin" }
    ]
});
db.getSiblingDB('$external').createUser({
    user: "CN=AGENT_USERNAME",
    roles: [
         { role: "clusterMonitor", db: "admin"  }
    ]
});
db.setProfilingLevel(1, { slowms: 100 });