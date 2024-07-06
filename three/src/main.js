import { startFrame } from "./frame.js";
import { WEBGL } from "./lib/webgl.js";
import { threeJS } from "./three.js";
// import * as THREE from "./lib/three.module.js";
// import { OrbitControls } from "./lib/OrbitControls.js";
// import { Desk } from "./lib/objects/desk.js";
// import { Room } from "./lib/objects/Room.js";
// import {
//   deskName,
//   floorName,
//   shadowName,
//   groundName,
//   ceilingName,
//   resizerName,
// } from "./lib/objectConf/objectNames.js";
// import {
//   deskColor,
//   floorColors,
//   shadowColor,
// } from "./lib/objectConf/colors.js";
// import { InitObjects } from "./lib/objects/sceneObjects.js";
// import { Floor } from "./lib/objects/floor.js";

if (WEBGL.isWebGLAvailable()) {
  startFrame();
  // threeJS();
} else {
  const warning = WEBGL.getWebGLErrorMessage();
  document.body.appendChild(warning);
}
