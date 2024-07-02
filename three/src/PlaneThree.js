import { floorColors } from "./lib/three/objectConf/colors";
import { floorName, roomName } from "./lib/three/objectConf/objectNames";
import { D2Room } from "./lib/three/objects/Room.js";
import { Circles } from "./lib/three/objects/circles.js";
import * as THREE from "./lib/three/three.module.js";
// import { OrbitControls } from "./lib/three/OrbitControls.js";
export const TwoDigitStart = () => {
  const btns = document.getElementById("hud-icon");

  // zoom in/out & drag and drop
  let isDragging = false;
  let previousMousePosition = { x: 0, y: 0 };
  const maxZoom = 500;
  const minZoom = 50;
  let cameraZoom = 250;

  // create object
  let isCreating = {
    isSelect: false,
    isDragging: false,
  };

  let isChangingObject = {
    isDBClick: false,
    isHover: false, //for mouse pointer and for clicked circle changer
    isDragging: false, //is changer circle moving ?
    changingObject: null, // changing object...
  };

  const objects = {};

  const mouse = new THREE.Vector2();
  const raycaster = new THREE.Raycaster();
  const scene = new THREE.Scene();
  const camera = new THREE.PerspectiveCamera(
    75,
    window.innerWidth / window.innerHeight,
    0.1,
    1000
  );
  const renderer = new THREE.WebGLRenderer();
  renderer.setSize(window.innerWidth, window.innerHeight);
  document.body.appendChild(renderer.domElement);
  //   const controls = new OrbitControls(camera, renderer.domElement);
  //   controls.update();
  // 카메라 설정
  camera.position.set(0, cameraZoom, 0);
  camera.lookAt(0, 0, 0);

  // 기본적인 그리드 헬퍼 추가
  scene.add(new THREE.AxesHelper(1000));
  const gridHelper = new THREE.GridHelper(2000, 100);
  scene.add(gridHelper);

  // 큐브 추가 (예시)
  const geometry = new THREE.BoxGeometry(30, 100, 50);
  const material = new THREE.MeshBasicMaterial({ color: 0x00ff00 });
  const cube = new THREE.Mesh(geometry, material);
  //   cube.rotation.x = Math.PI / 2;
  //   cube.rotation.y = -Math.PI / 2;
  //   cube.rotation.x = Math.PI / 2;
  cube.position.set(0, 0, 0);
  scene.add(cube);

  const planeGeometry = new THREE.PlaneGeometry(2000, 2000);
  const planeMaterial = new THREE.MeshBasicMaterial({
    color: 0xffff00,
    side: THREE.DoubleSide,
  });
  const plane = new THREE.Mesh(planeGeometry, planeMaterial);
  plane.position.set(0, -1, 0);
  plane.rotation.x = -Math.PI / 2; // x축을 기준으로 90도 회전
  plane.name = "background";
  objects["background"] = plane;
  scene.add(plane);

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

  const createObject = ({ name, points }) => {
    const room = new D2Room({ points });
    room.name = name;
    const circleGroup = new Circles({ points });
    room.add(circleGroup);
    const id = name === "shadow" ? "shadow" : room.uuid;

    objects[id] = {
      coords: points,
      obj: room,
      name: name,
    };
    scene.add(room);
  };

  const onMouseDown = (event) => {
    let points;
    switch (true) {
      case isChangingObject.isDBClick && isChangingObject.isHover:
        // 오브젝트의 넓이를 드래그(circle)을 통해 옮기고 있는지
        isChangingObject = {
          ...isChangingObject,
          isDragging: true,
        };
        return;

      case isCreating.isSelect && isCreating.isDragging:
        // not shadow. create real object
        points = [...objects["shadow"].coords];
        const name = roomName;
        createObject({ name, points });
        scene.remove(scene.children.find((obj) => obj.name === "shadow"));
        isCreating = {
          isSelect: false,
          isDragging: false,
        };
        return;

      case isCreating.isSelect:
        // create shadow for dragging
        const raycaster = getIntersects(event);
        const background = raycaster.find(
          (obj) => obj.object.name === "background"
        );
        if (!background) return;
        points = [
          new THREE.Vector3(background.point.x, 0, background.point.z),
          new THREE.Vector3(background.point.x + 20, 0, background.point.z),
          new THREE.Vector3(
            background.point.x + 20,
            0,
            background.point.z + 20
          ),
          new THREE.Vector3(background.point.x, 0, background.point.z + 20),
        ];
        createObject({ points, name: "shadow" });
        isCreating = {
          ...isCreating,
          isDragging: true,
        };
        return;

      default:
        isDragging = true;
        previousMousePosition = { x: event.clientX, y: event.clientY };
        return;
    }
  };

  const onMouseMove = (event) => {
    const raycaster = getIntersects(event);
    switch (true) {
      case isChangingObject.isDBClick &&
        isChangingObject.isDragging &&
        isChangingObject.isHover:
        console.log("dragging...");
        return;
      case isChangingObject.isDBClick:
        const circle = raycaster.find((obj) => obj.object.name === "circle");
        if (!circle) {
          //   오브젝트(room)의 체인징 원에 커서를 안올려놨을 경우
          document.body.style.cursor = "default";
          isChangingObject = {
            ...isChangingObject,
            isHover: false,
          };
        } else {
          //   오브젝트(room)의 체인징 원에 커서를 올려놨을 경우
          isChangingObject = {
            ...isChangingObject,
            isHover: true,
          };
          document.body.style.cursor = "pointer";
        }
        return;

      case isCreating.isSelect && isCreating.isDragging:
        const background = raycaster.find(
          (obj) => obj.object.name === "background"
        );
        if (!background) return;

        //   const originPoint = background.point;
        const originPoint = { ...objects["shadow"].coords[0] };

        const points = [
          new THREE.Vector3(originPoint.x, 0, originPoint.z),
          new THREE.Vector3(background.point.x, 0, originPoint.z),
          new THREE.Vector3(background.point.x, 0, background.point.z),
          new THREE.Vector3(originPoint.x, 0, background.point.z),
        ];

        scene.remove(objects["shadow"].obj);
        createObject({ name: "shadow", points });
        return;

      case isDragging:
        const deltaMove = {
          x: event.clientX - previousMousePosition.x,
          y: event.clientY - previousMousePosition.y,
        };

        // 카메라 위치 업데이트
        camera.position.x -= deltaMove.x / (maxZoom / cameraZoom); // 마우스 이동 속도와 동일하게 업데이트
        camera.position.z -= deltaMove.y / (maxZoom / cameraZoom); // 마우스 이동 속도와 동일하게 업데이트
        camera.lookAt(camera.position.x, 0, camera.position.z);

        previousMousePosition = { x: event.clientX, y: event.clientY };
        return;
      default:
        return;
    }
  };

  const onDoubleClick = (event) => {
    const raycaster = getIntersects(event);
    const floor = raycaster.find((obj) => obj.object.name === "floor");
    // console.log(floor);
    if (!floor) return;
    const circleGroup = floor.object.parent.children.find(
      (obj) => obj.name === "circleGroup"
    );
    if (!circleGroup) return;
    circleGroup.visible = true;

    isChangingObject = {
      ...isChangingObject,
      changingObject: floor,
      isDBClick: true,
    };
  };

  const onMouseUp = () => {
    // if (
    //   isChangingObject.isDBClick &&
    //   isChangingObject.isHover &&
    //   isChangingObject.isDragging
    // ) {
    //   isChangingObject = {
    //     isDBClick: false,
    //     isHover: false,
    //     isDragging: false,
    //     changingObject: null,
    //   };
    // }
    //   if(Object(isChangingObject).values())
    if (
      isChangingObject.isDBClick &&
      isChangingObject.isDragging &&
      isChangingObject.isHover
    ) {
      isChangingObject = {
        ...isChangingObject,
        isDragging: false,
      };
    }
    isDragging = false;
  };

  const onWheel = (event) => {
    const delta = event.deltaY;
    const newZoom = Math.min(maxZoom, Math.max(minZoom, cameraZoom - delta));
    cameraZoom = newZoom;
    camera.position.y = newZoom;
    camera.lookAt(camera.position.x, 0, camera.position.z);
  };

  const onCreate = (e) => {
    const btn = e.target.closest("button");
    if (!btn) return;
    isCreating = {
      ...isCreating,
      isSelect: true,
    };
  };

  // 이벤트 리스너 추가
  window.addEventListener("mousedown", onMouseDown);
  window.addEventListener("mousemove", onMouseMove);
  window.addEventListener("mouseup", onMouseUp);
  window.addEventListener("wheel", onWheel);
  btns.addEventListener("click", onCreate);
  window.addEventListener("dblclick", onDoubleClick);

  // 애니메이션 루프
  const animate = () => {
    // controls.update();
    requestAnimationFrame(animate);
    renderer.render(scene, camera);
  };

  animate();
};
