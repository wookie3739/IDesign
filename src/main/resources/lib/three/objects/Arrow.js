import { THREE } from "../loader/three";
import { arrowsY } from "../objectConf/renderOrders";

export class Arrow extends THREE.Mesh {
  constructor({ width, angle, position }) {
    const shape = new THREE.Shape();
    const arrowWidth = 2;
    const arrowHeight = 2;
    const shaftWidth = width - arrowHeight * 5;
    const shaftHeight = 1;

    // 좌우 화살표 그리기
    shape.moveTo(-shaftWidth / 2, shaftHeight / 2);
    shape.lineTo(-shaftWidth / 2, arrowHeight);
    shape.lineTo(-shaftWidth / 2 - arrowWidth, 0);
    shape.lineTo(-shaftWidth / 2, -arrowHeight);
    shape.lineTo(-shaftWidth / 2, -shaftHeight / 2);

    shape.lineTo(shaftWidth / 2, -shaftHeight / 2);
    shape.lineTo(shaftWidth / 2, -arrowHeight);
    shape.lineTo(shaftWidth / 2 + arrowWidth, 0);
    shape.lineTo(shaftWidth / 2, arrowHeight);
    shape.lineTo(shaftWidth / 2, shaftHeight / 2);

    shape.lineTo(-shaftWidth / 2, shaftHeight / 2);

    const geometry = new THREE.ShapeGeometry(shape);
    const material = new THREE.MeshBasicMaterial({
      color: "rgb(134, 134, 134)",
      side: THREE.DoubleSide,
    });
    super(geometry, material);
    this.userData = {
      ...this.userData,
    };
    this.position.set(position.x, arrowsY, position.z);
    this.rotation.x = -Math.PI / 2;
    this.rotation.z = -angle;
  }
}
