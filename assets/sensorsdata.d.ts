/*
 * @Date: 2023-05-19 21:39:39
 * @File: 
 */
declare namespace sensorsDataAPI {
    function init(c:object): void;
    function track(e: string, p?: object): void;
    function setPara (p: object): void;
  
    function identify(id: string): void;
    function login(id: string): void;
    function logout(): void;
  
    function setOnceProfile(prop: object): void;
    function setProfile(prop: object): void;
  
    function register(prop: object): void;
    function clearRegister(list: Array<string>): void;
  
    function trackAppInstall(prop: object): void;
    function flush(): void;
    function getPresetProperties(callback?: any): any;
    function deleteAll(): void;
  }