import { THREE } from "../loader/three";
import { circleGroupName, circleName } from "../objectConf/objectNames";
import { circleY, circlesRenderOrder, roomY } from "../objectConf/renderOrders";
import { uuidv4 as uuid } from "../uuid";

export class Circles extends THREE.Group {
  constructor({ points }) {
    super();
    this.userData = {
      ...this.userData,
      oid: uuid(),
    };
    this.name = circleGroupName;
    points.forEach((point) => {
      const geometry = new THREE.CircleGeometry(15, 32);
      const material = new THREE.MeshBasicMaterial({
        color: "rgb(221,220,220)",
        side: THREE.DoubleSide,
      });
      const circle = new THREE.Mesh(geometry, material);
      circle.name = circleName;
      circle.position.set(point.x, circleY, point.z);
      circle.rotation.x = -Math.PI / 2;
      circle.userData = {
        ...circle.userData,
        oid: uuid(),
      };
      this.add(circle);
      this.rednerOrder = circlesRenderOrder;
    });
  }
}