// routes.js

import Home from './pages/Login/App';
import Login from './pages/Login/App';
import Register from './pages/Registration/App';
import Profile from './pages/Profile/App';
import EditProfile from './pages/EditProfile/User';
import Admin from './pages/AdminProfile/App';
import AdminDashboard from './pages/AdminDashboard/App';
import CreateActivity from './pages/CreateActivity/App';
import Activity from './pages/ViewActivity/App';
import EditActivity from "./pages/EditActivity/EditActivity";
import ListActivity from './pages/ListActivity/App';
import Search from './pages/Search/App';
import HashTag from './pages/HashTag/App';
import HomeFeed from './pages/HomeFeed/App';

const routes = [
    {path: '/', component: Home},
    {path: '/register', component: Register},
    {path: '/login', component: Login},
    {path: '/admin', component: Admin},
    {path: '/admin/dashboard', component: AdminDashboard, name: 'AdminDashboard'},
    {path: '/profiles/:id', component: Profile},
    {path: '/profiles/edit/:id', component: EditProfile},
    {path: '/profiles/:id/activities/create', component: CreateActivity},
    {path: '/profiles/:id/activities/:activityId/edit', component: EditActivity},
    {path: '/profiles/:id/activities/:activityId', component: Activity},
    {path: '/profiles/:id/activities/', component: ListActivity},
    {path: '/profiles', component: Search, name: 'Users' },
    {path: '/hashtag/:hashtag', component: HashTag },
    {path: '/home', component: HomeFeed }
    ];

export default routes;