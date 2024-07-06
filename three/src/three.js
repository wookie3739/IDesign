import * as THREE from "./lib/three/three.module.js";
import { OrbitControls } from "./lib/three/OrbitControls.js";
import { WEBGL } from "./lib/webgl.js";
import { Desk } from "./lib/three/objects/desk.js";
import { Room } from "./lib/three/objects/Room.js";

import { InitObjects } from "./lib/three/objects/sceneObjects.js";
import {
  deskName,
  floorName,
  shadowName,
  groundName,
  ceilingName,
  resizerName,
} from "./lib/three/objectConf/objectNames.js";
import {
  deskColor,
  floorColors,
  shadowColor,
} from "./lib/three/objectConf/colors.js";
// import { Floor } from "./lib/objects/floor.js";

export const threeJS = () => {
  const btn = document.getElementById("hud-icon");
  btn.style.display = "block";
  let selectedObject = null;

  const objects = {};
  let isChangingSize = false;
  let resizeGroup = null;
  let resizer = null;

  const scene = new THREE.Scene();
  const mouse = new THREE.Vector2();
  const raycaster = new THREE.Raycaster();
  scene.background = new THREE.Color("white");
  const camera = new THREE.PerspectiveCamera(
    75,
    window.innerWidth / window.innerHeight,
    0.01,
    1000
  );
  camera.position.z = 10;
  camera.position.y = 5;
  camera.position.x = 10;
  camera.lookAt(0, 0, 0);
  const renderer = new THREE.WebGLRenderer({ antialias: true });
  renderer.setSize(window.innerWidth, window.innerHeight);
  document.body.appendChild(renderer.domElement);
  const controls = new OrbitControls(camera, renderer.domElement);
  controls.update();

  const { axesHelper, initRoom } = new InitObjects();
  // const { floor } = new Floor({});
  scene.add(axesHelper);
  scene.add(initRoom);
  const wallsAndCeiling = [];
  initRoom.children.forEach((child) => {
    if (child.material && child.material instanceof THREE.MeshBasicMaterial) {
      if (child.rotation.x !== -Math.PI / 2) {
        // 바닥을 제외
        child.material.transparent = true;
        wallsAndCeiling.push(child);
      }
    }
  });

  const animate = () => {
    requestAnimationFrame(animate);
    controls.update();
    renderer.render(scene, camera);
  };

  const createDesk = (x, z, object, color, condition) => {
    const desk = new Desk({
      x,
      z,
      object,
      width: 4,
      height: 1.5,
      depth: 2,
      color,
    });
    desk.children.forEach((child) => {
      if (child.material && child.material instanceof THREE.MeshBasicMaterial) {
        child.material.transparent = false;
        child.material.opacity = 1;
      }
    });
    if (condition) {
      objects[shadowName] = desk;
    } else {
      objects[desk.uuid] = desk;
    }
    scene.add(desk);
  };

  const getIntersects = (event) => {
    event.preventDefault();

    // 마우스 포인트 계산
    mouse.x = (event.clientX / window.innerWidth) * 2 - 1;
    mouse.y = -(event.clientY / window.innerHeight) * 2 + 1;
    // Raycaster 설정
    raycaster.setFromCamera(mouse, camera);

    // 평면과의 교차점 계산

    return raycaster.intersectObjects(scene.children, true);
  };

  const getSize = (object) => {
    // return { x: 20, y: 1, z: 20 };
    const boundingBox = new THREE.Box3().setFromObject(object);

    // 크기 계산
    const size = new THREE.Vector3();
    boundingBox.getSize(size);
    return size;
  };

  const createFloor = (intersects, floorColor, isShadow, event) => {
    let selectedObject = intersects.find(
      (obj) => obj.object.name === floorName
    );
    if (selectedObject) {
      const size = getSize(selectedObject.object);
      let x = selectedObject.point.x > 0 ? 1 : -1;
      let z = selectedObject.point.z > 0 ? 1 : -1;
      const y = isShadow ? 0.1 : 0;

      // const cx = x;
      // const cy = z;

      x = (selectedObject.object.position.x + size.x / 2) * x;
      z = (selectedObject.object.position.z + size.z / 2) * z;

      const floor = new Room({ x, y, z, floorColor });
      objects[shadowName] = floor;
      scene.add(floor);
    } else {
      selectedObject = intersects.find((obj) => obj.object.name === groundName);
    }

    if (isShadow) {
    }
  };

  const creator = (event, shadowName, color) => {
    if (objects.hasOwnProperty(shadowName)) {
      scene.remove(objects[shadowName]);
      delete objects[shadowName];
    }
    const intersects = getIntersects(event);

    if (selectedObject === floorName) {
      const floorColor = shadowName ? shadowColor : color;
      // const isShadow = event.type === "mousemove";
      createFloor(intersects, floorColor, shadowName, event);
      return;
    }
    if (intersects.length < 1) {
      return;
    }
    const conditionName = selectedObject === deskName ? floorName : "null";
    const clickedObject = intersects.find(
      (intersect) => intersect.object.name === conditionName
    );
    if (!clickedObject) return;
    const intersect = clickedObject.object;
    createDesk(
      clickedObject.point.x,
      clickedObject.point.z,
      intersect,
      color,
      shadowName
    );
  };

  const onDocumentMouseDown = (event) => {
    // console.log("moving!!");
    if (controls.enabled) return;
    // console.log(resizer);
    // if (resizer) {
    //   console.log(isChangingSize);
    //   const intersects = getIntersects(event);
    //   console.log(intersects);
    //   return;
    // }
    if (!selectedObject) return;
    let color = null;

    // create some object via btns
    switch (selectedObject) {
      case deskName:
        color = deskColor;
        break;
      case floorName:
        color = floorColors;
        break;
    }
    creator(event, false, color);
  };

  const onDoubleClick = (e) => {
    // resize object via "resizer" obj
    const intersects = getIntersects(e);

    controls.enabled = false;
    if (intersects.length === 0) return;

    const intersect = intersects[0].object.parent;
    if (!intersect) return;
    const resize = intersect.children.find(
      (child) => child.name === resizerName
    );
    if (!resize) return;
    resize.visible = true;
    if (!resizer) return;
    resizer = resize;
    resizeGroup = intersect;
  };

  const onDocumentMouseMove = (event) => {
    if (controls.enabled) return;
    if (!selectedObject) return;
    switch (selectedObject) {
      case deskName:
        break;
      case floorName:
        break;
    }
    creator(event, shadowName, shadowColor);
  };

  const onKeyDown = (event) => {
    const { keyCode } = event;
    switch (keyCode) {
      case 27:
        controls.enabled = true;
        if (objects.hasOwnProperty(shadowName)) {
          scene.remove(objects[shadowName]);
          delete objects[shadowName];
        }
        break;
      default:
        break;
    }
  };

  const onClickIcon = (event) => {
    event.stopPropagation();
    const btn = event.target.closest("button");

    if (!btn) return;
    controls.enabled = false;
    switch (btn.innerText) {
      case "책상":
        selectedObject = deskName;
        break;
      case "바닥":
        selectedObject = floorName;
        break;
      default:
        break;
    }
  };

  btn.addEventListener("mousedown", onClickIcon);
  document.addEventListener("mousemove", onDocumentMouseMove, false);
  document.addEventListener("mousedown", onDocumentMouseDown, false);
  document.addEventListener("keydown", onKeyDown, false);
  document.addEventListener("dblclick", onDoubleClick);

  const onWindowResize = () => {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(window.innerWidth, window.innerHeight);
  };

  window.addEventListener("resize", onWindowResize, false);
  animate();
};
