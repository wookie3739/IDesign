import axios from "axios";
import { interectionObserver } from "../lib/observer.js";
import { dummy } from "../lib/dashboard/dummy.js";
import { projectNode } from "../lib/dashboard/projectNode.js";
import { debounce } from "../lib/debounce.js";

const side = document.getElementById("side");
const sidebarArrow = document.getElementById("arrow-box");
const searchInput = document.getElementById("searching-input");
const sortBox = document.getElementById("selected");
const selectedSort = sortBox.getElementsByTagName("span")[0];
const options = document.getElementById("select-options");
const container = document.getElementById("container");
const gridWrapper = document.getElementById("grid-wrapper");
const trashBtn = document.getElementById("trash");
const createProjectBtn = document.getElementById("create-project");
const observer = document.getElementById("observer");
const projectDescriptionContainer = document.getElementById("pdc");
const projectDescriptionBG = document.getElementById("pdbg");
const submitProject = document.getElementById("create-p-btn");
const textArea = document.getElementById("description-des");
const optionHeight = 200;

let isLaoding = false;
let hasMoreProjects = true;
let isTrashBtnClick = false;

let projects = {};
let remove_projects = {};

const removeAllProject = () => {
  const projects = Array.from(gridWrapper.getElementsByClassName("project"));
  projects.forEach((box) => {
    if (box.classList.contains("received")) {
      gridWrapper.removeChild(box);
    }
  });
};

const getDummyData = () => {
  return new Promise((res) => {
    setTimeout(() => {
      res(dummy);
    }, 1);
  });
};

const getProjects = async () => {
  if (!hasMoreProjects || isLaoding) return;
  isLaoding = true;

  const filter = searchInput.value;
  const sort = selectedSort.id;
  let data;
  try {
    data = await axios.get("/api/get/projects", {
      params: { filter, sort },
    });
    data = data.data;
  } catch (err) {
    console.log("error occured!", err);
    data = await getDummyData();
  }
  if (data.status === "fail") {
    console.log("error occured!", err);
    return;
  }
  isLaoding = false;
  const receivedProjects = data.data.projects;
  if (data.data.flagNum === -1) {
    observer.style.display = "none";
  }
  const newProjects = {};
  receivedProjects.forEach((obj) => {
    newProjects[obj.project_id] = obj;
  });

  projects = {
    ...projects,
    ...newProjects,
  };
  receivedProjects.forEach((project) => {
    //새 프로젝트 html에 추가
    const node = projectNode(project);
    gridWrapper.appendChild(node);
  });
};

const onFilterChange = () => {
  removeAllProject();
  getProjects();
};

const onChangeSort = (e) => {
  const li = e.target.closest("li");
  if (!li) return;

  selectedSort.innerText = li.innerText;
  selectedSort.id = li.id;

  removeAllProject();
  getProjects();
};

const onSortClick = (e) => {
  e.stopPropagation();
  if (options.style.display === "block") {
    options.style.display = "none";
    return;
  }
  const offset = 15;
  const heightRect = sortBox.getClientRects()[0];
  const bottom = window.innerHeight - heightRect.bottom;
  if (bottom > optionHeight) {
    options.style.bottom = 0;
    options.style.top = sortBox.offsetHeight + offset + "px";
  } else {
    options.style.top = 0;
    options.style.bottom = sortBox.offsetHeight + offset + "px";
  }
  options.style.display = "block";
};

const onChangeSideBar = () => {
  const left = sidebarArrow.getElementsByClassName("left")[0];
  const right = sidebarArrow.getElementsByClassName("right")[0];
  if (side.classList.contains("spread")) {
    left.style.display = "none";
    right.style.display = "block";
    side.classList.remove("spread");
    side.classList.add("fold");
  } else {
    right.style.display = "none";
    left.style.display = "block";
    side.classList.remove("fold");
    side.classList.add("spread");
  }
};

const reqRemoveProjects = async () => {
  // 프로젝트 삭제
  console.log(remove_projects);

  try {
    // project_ids 파라미터를 쿼리 스트링으로 직접 구성
    const queryString = Object.keys(remove_projects)
      .map((key) => `project_ids=${key}`)
      .join("&");
    console.log(queryString);

    const url = `/api/remove/projects?${queryString}`;

    // DELETE 요청에 URL로 직접 전달
    const data = await axios.delete(url);

    if (data.status === "fail") {
      alert("오류가 발생했습니다. 다시 시도해주세요.");
      return;
    }
  } catch (err) {
    Object.keys(remove_projects).forEach((project_id) => {
      delete projects[project_id];
      document.getElementById(project_id).remove();
    });

    remove_projects = {};
    removeBackgroundChanger("none");
    alert("오류가 발생했습니다. 다시 시도해주세요.");
  }
};

const removeBackgroundChanger = (style) => {
  const nodes = Array.from(document.getElementsByClassName("delete_bg"));
  nodes.forEach((node) => (node.style.display = style));
};

const onTrashClick = () => {
  isTrashBtnClick = !isTrashBtnClick;
  if (isTrashBtnClick) {
    removeBackgroundChanger("block");
  } else {
    reqRemoveProjects();
  }
};

const onProjectClick = (e) => {
  const btn = e.target.closest("button");
  if (!btn) return;
  const project_id = btn.name;
  const input = btn.getElementsByTagName("input")[0];
  input.checked = !input.checked;

  if (input.checked) {
    remove_projects[project_id] = project_id;
  } else {
    delete remove_projects[project_id];
  }
};

const onClickWindow = () => {
  if (options.style.display === "block") {
    options.style.display = "none";
    return;
  }
};

const onCreateProject = () => {
  //프로젝트 타이틀, 설명 팝업
  let s = "block";
  if (projectDescriptionContainer.style.display === "block") {
    s = "none";
  }
  projectDescriptionContainer.style.display = s;
};
const onSubmitProject = async () => {
  //프로젝트 생성(타이틀, 설명)
  const title = document.getElementById("description-title").value;

  const src = textArea.value;

  // const obj = { title, src };
  try {
    const data = await axios.post("/api/create_project", {
      src,
      title,
    });
    console.log("data: ", data);
    if (data.status === "fail") {
      return alert("공습경보");
    }

    // data: null; => 에러
    // data:{project_id:1} => 에러안남
    const { project_id } = data.data;

    window.location.href = `${window.location.origin}/three?project_id=${project_id}`;
  } catch (e) {
    alert("공습경고", e);
  }
};
interectionObserver(observer, getProjects);

container.addEventListener("click", onProjectClick);
submitProject.addEventListener("click", onSubmitProject);
projectDescriptionBG.addEventListener("click", onCreateProject);
createProjectBtn.addEventListener("click", onCreateProject);
trashBtn.addEventListener("click", onTrashClick);
sortBox.addEventListener("click", onSortClick);
options.addEventListener("click", onChangeSort);
sidebarArrow.addEventListener("click", onChangeSideBar);
window.addEventListener("click", onClickWindow);
searchInput.addEventListener("input", debounce(onFilterChange, 200));

window.removeEventListener("beforeunload", onClickWindow);
